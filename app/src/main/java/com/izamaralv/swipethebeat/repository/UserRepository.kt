package com.izamaralv.swipethebeat.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance().apply {
        Log.d("Firestore", "Firestore instance initialized: $this")
    }

    /**
     * Guarda los datos básicos del usuario tras el login.
     * ▶ Ahora SIN pisar jamás los artistas favoritos.
     */
    fun saveUserToFirestore(userData: Map<String, String>) {
        val userId = userData["user_id"]
        if (userId.isNullOrEmpty()) {
            Log.e("Firestore", "❌ Invalid user ID! Cannot save user data.")
            return
        }

        // 1. Leemos el color existente para preservarlo si no viene en userData
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val existingColor = document.getString("profile_color") ?: "#1DB954"

                // 2. Construimos sólo los campos que realmente vienen del login
                // ▶ Quitamos favorite_artist1–3 de aquí para no resetearlos
                val user = hashMapOf(
                    "user_id"      to userId,
                    "name"         to userData["name"],
                    "email"        to userData["email"],
                    "avatar_url"   to userData["avatar_url"],
                    "profile_color" to (userData["profile_color"] ?: existingColor)
                )

                Log.d("Firestore", "🔍 Attempting to save user: $userId with data: $user")

                // 3. Usamos merge para no borrar ningún campo extra (p.ej. artistas favoritos)
                firestore.collection("users")
                    .document(userId)
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("Firestore", "✅ User data saved successfully for ID: $userId")
                    }
                    .addOnFailureListener { error ->
                        Log.e("Firestore", "❌ Firestore permission error: ${error.message}")
                    }
            }
    }

    /**
     * Actualiza un slot de artista favorito (0→favorite_artist1, 1→favorite_artist2, 2→favorite_artist3)
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
                Log.e(
                    "Firestore",
                    "❌ Error updating $field for user $userId: ${e.message}"
                )
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

                    // ▶ Cargamos también los 3 artistas favoritos
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
     * Si el usuario NO existe, lo creamos con todos los campos esenciales de inicio:
     * → profile_color por defecto y tres slots vacíos de artista.
     */
    fun initializeUserProfile(userId: String, userData: Map<String, String>) {
        val userRef = firestore.collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                // ▶ Definimos aquí TODOS los campos iniciales de una sola vez
                val fullUser = hashMapOf(
                    "user_id"          to userId,
                    "name"             to userData["name"],
                    "email"            to userData["email"],
                    "avatar_url"       to userData["avatar_url"],
                    "profile_color"    to (userData["profile_color"] ?: "#1DB954"),
                    "favorite_artist1" to "",
                    "favorite_artist2" to "",
                    "favorite_artist3" to ""
                )
                userRef.set(fullUser)
                    .addOnSuccessListener {
                        Log.d("Firestore", "✅ New user profile created: $fullUser")
                        // placeholder liked_songs…
                        firestore.collection("users")
                            .document(userId)
                            .collection("liked_songs")
                            .document("initial_song")
                            .set(mapOf("info" to "Liked songs will be stored here"))
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "❌ Error creating user profile: ${e.message}")
                    }
            } else {
                Log.d("Firestore", "✅ User already exists in Firestore: $userId")
            }
        }
    }

    /**
     * Actualiza únicamente el color de perfil (igual que antes).
     */
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
}
