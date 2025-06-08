package com.izamaralv.swipethebeat.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Acceso a Firestore para usuarios, canciones guardadas y recomendaciones.
 */
class UserRepository {

    private val firestore = FirebaseFirestore.getInstance().apply {
        Log.d("UserRepository", "Instancia Firestore inicializada")
    }

    /** Crea o actualiza los datos básicos del usuario (sin tocar artistas favoritos). */
    fun saveUserToFirestore(
        userData: Map<String, String>,
        onComplete: (() -> Unit)? = null
    ) {
        val userId = userData["user_id"].orEmpty()
        if (userId.isEmpty()) return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val existingColor = doc.getString("profile_color") ?: "#1DB954"
                val user = hashMapOf(
                    "user_id" to userId,
                    "name" to userData["name"],
                    "email" to userData["email"],
                    "avatar_url" to userData["avatar_url"],
                    "profile_color" to (userData["profile_color"] ?: existingColor)
                )
                firestore.collection("users")
                    .document(userId)
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener { onComplete?.invoke() }
            }
    }

    /** Modifica uno de los tres artistas favoritos del usuario. */
    fun updateFavoriteArtist(userId: String, slot: Int, artist: String) {
        val field = "favorite_artist${slot + 1}"
        firestore.collection("users")
            .document(userId)
            .update(field, artist)
    }

    /** Obtiene todos los datos del usuario, incluidos los artistas favoritos. */
    fun getUserFromFirestore(userId: String?, onResult: (Map<String, String>?) -> Unit) {
        if (userId.isNullOrEmpty()) {
            onResult(null); return
        }
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    onResult(null); return@addOnSuccessListener
                }
                val data = mutableMapOf<String, String>()
                listOf("user_id", "name", "email", "avatar_url", "profile_color").forEach {
                    doc.getString(it)?.let { v -> data[it] = v }
                }
                (1..3).forEach { i ->
                    data["favorite_artist$i"] = doc.getString("favorite_artist$i") ?: ""
                }
                onResult(data)
            }
            .addOnFailureListener { onResult(null) }
    }

    /** Añade una canción a la subcolección `saved_songs`. */
    fun addSavedSong(userId: String, songData: Map<String, String>) {
        val docId = songData["id"] ?: UUID.randomUUID().toString()
        firestore.collection("users")
            .document(userId)
            .collection("saved_songs")
            .document(docId)
            .set(songData)
    }

    /** Elimina una canción de `saved_songs`. */
    fun deleteSavedSong(userId: String, songId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("saved_songs")
            .document(songId)
            .delete()
    }

    /** Recupera las canciones marcadas para más tarde (no las recomendaciones). */
    fun getSavedSongs(userId: String, onResult: (List<Map<String, String>>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("saved_songs")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { it.data?.mapValues { it.value.toString() } }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    /** Guarda en el documento del usuario la lista actual de recomendaciones. */
    fun saveRecommendations(userId: String, recs: List<Map<String, String>>) {
        firestore.collection("users")
            .document(userId)
            .update("recommendations", recs)
    }

    /** Carga las recomendaciones del usuario desde Firestore. */
    fun loadRecommendations(userId: String, onComplete: (List<Map<String, String>>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val list = doc.get("recommendations") as? List<Map<String, String>> ?: emptyList()
                onComplete(list)
            }
            .addOnFailureListener { onComplete(emptyList()) }
    }

    /** Versión suspend para cargar recomendaciones sin callback. */
    suspend fun loadRecommendationsSuspend(userId: String): List<Map<String, String>> =
        try {
            val snap = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            @Suppress("UNCHECKED_CAST")
            snap.get("recommendations") as? List<Map<String, String>> ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

    /** Guarda el color del perfil en Firestore. */
    fun updateUserColor(userId: String, newColor: String) {
        firestore.collection("users")
            .document(userId)
            .update("profile_color", newColor)
    }
}
