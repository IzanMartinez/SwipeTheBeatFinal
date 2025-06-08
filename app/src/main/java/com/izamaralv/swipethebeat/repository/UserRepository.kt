package com.izamaralv.swipethebeat.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance().apply {
        Log.d("UserRepository", "Firestore instance inicializada: $this")
    }

    /**
     * Guarda los datos básicos del usuario tras el login.
     * ▶ Ahora sin pisar jamás los artistas favoritos.
     */
    fun saveUserToFirestore(
        userData: Map<String, String>,
        onComplete: (() -> Unit)? = null
    ) {
        val userId = userData["user_id"]
        if (userId.isNullOrEmpty()) {
            Log.e("Firestore", "❌ Invalid user ID! Cannot save user data.")
            return
        }

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val existingColor = document.getString("profile_color") ?: "#1DB954"
                val user = hashMapOf(
                    "user_id"       to userId,
                    "name"          to userData["name"],
                    "email"         to userData["email"],
                    "avatar_url"    to userData["avatar_url"],
                    "profile_color" to (userData["profile_color"] ?: existingColor)
                )

                Log.d("Firestore", "🔍 Attempting to save user: $userId with data: $user")
                firestore.collection("users")
                    .document(userId)
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("Firestore", "✅ User data saved successfully for ID: $userId")
                        onComplete?.invoke()
                    }
                    .addOnFailureListener { error ->
                        Log.e("Firestore", "❌ Firestore permission error: ${error.message}")
                    }
            }
    }

    /**
     * Actualiza un slot de artista favorito (0→favorite_artist1, 1→favorite_artist2, 2→favorite_artist3).
     */
    fun updateFavoriteArtist(userId: String, slot: Int, artist: String) {
        val field = "favorite_artist${slot + 1}"
        Log.d("Firestore", "Updating $field for user $userId to '$artist'")
        firestore.collection("users")
            .document(userId)
            .update(field, artist)
            .addOnSuccessListener {
                Log.d("Firestore", "✅ $field updated successfully for user $userId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error updating $field for user $userId: ${e.message}")
            }
    }

    /**
     * Recupera todos los datos del usuario, incluyendo favorite_artist1–3.
     */
    fun getUserFromFirestore(userId: String?, onResult: (Map<String, String>?) -> Unit) {
        if (userId.isNullOrEmpty()) {
            Log.e("Firestore", "Invalid user ID: $userId")
            onResult(null)
            return
        }

        Log.d("Firestore", "Fetching user profile from Firestore for ID: $userId")
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = mutableMapOf<String, String>()
                    document.getString("user_id")?.let    { userData["user_id"] = it }
                    document.getString("name")?.let       { userData["name"] = it }
                    document.getString("email")?.let      { userData["email"] = it }
                    document.getString("avatar_url")?.let { userData["avatar_url"] = it }
                    document.getString("profile_color")?.let { userData["profile_color"] = it }
                    for (i in 1..3) {
                        val field = "favorite_artist$i"
                        userData[field] = document.getString(field) ?: ""
                    }
                    Log.d("Firestore", "User data retrieved: $userData")
                    onResult(userData)
                } else {
                    Log.e("Firestore", "User not found in Firestore!")
                    onResult(null)
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error fetching user data: ${it.message}")
                onResult(null)
            }
    }

    /**
     * Inserta una canción en la subcolección "saved_songs" del usuario.
     */
    fun addSavedSong(userId: String, songData: Map<String, String>) {
        val documentId = songData["id"] ?: UUID.randomUUID().toString()
        firestore.collection("users")
            .document(userId)
            .collection("saved_songs")
            .document(documentId)
            .set(songData)
    }

    /**
     * Borra una canción guardada.
     */
    fun deleteSavedSong(userId: String, songId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("saved_songs")
            .document(songId)
            .delete()
    }

    /**
     * Recupera las canciones guardadas (no confundir con recomendaciones).
     */
    fun getSavedSongs(userId: String, onResult: (List<Map<String, String>>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("saved_songs")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.data?.mapValues { it.value.toString() }
                }
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    /**
     * Guarda la lista de recomendaciones en Firestore bajo el campo "recommendations".
     */
    fun saveRecommendations(userId: String, recs: List<Map<String, String>>) {
        Log.d("UserRepository", "Saving recommendations for user $userId: $recs")
        firestore.collection("users")
            .document(userId)
            .update("recommendations", recs)
            .addOnSuccessListener {
                Log.d("UserRepository", "✅ Recommendations saved for user $userId")
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "❌ Error saving recommendations: ${e.message}")
            }
    }

    fun updateUserColor(userId: String, color: String) {
        firestore.collection("users")
            .document(userId)
            .update("profile_color", color)
            .addOnSuccessListener {
                Log.d("Firestore", "✅ Profile color updated successfully to: $color")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error updating profile color: ${e.message}")
            }
    }

    /**
     * Carga la lista de recomendaciones desde Firestore.
     */
    fun loadRecommendations(userId: String, onComplete: (List<Map<String, String>>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val list = doc.get("recommendations") as? List<Map<String, String>> ?: emptyList()
                Log.d("UserRepository", "Loaded recommendations for $userId: $list")
                onComplete(list)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "❌ Error loading recommendations: ${e.message}")
                onComplete(emptyList())
            }
    }

    suspend fun loadRecommendationsSuspend(userId: String): List<Map<String, String>> =
        try {
            val snap = firestore
                .collection("users")
                .document(userId)
                .get()
                .await()

            @Suppress("UNCHECKED_CAST")
            snap.get("recommendations") as? List<Map<String, String>> ?: emptyList()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error loading recommendations", e)
            emptyList()
        }
}



