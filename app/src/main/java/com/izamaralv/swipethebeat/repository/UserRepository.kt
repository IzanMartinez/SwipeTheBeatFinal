package com.izamaralv.swipethebeat.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance().apply {
        Log.d("Firestore", "Firestore instance initialized: $this")
    }

    fun saveUserToFirestore(userData: Map<String, String>) {
        val userId = userData["user_id"]

        if (userId.isNullOrEmpty()) {
            Log.e("Firestore", "❌ Invalid user ID! Cannot save user data.")
            return
        }

        val user = hashMapOf(
            "user_id" to userId,
            "name" to userData["name"],
            "email" to userData["email"],
            "avatar_url" to userData["avatar_url"]
        )

        Log.d("Firestore", "🔍 Attempting to save user: $userId with data: $user")

        firestore.collection("users")
            .document(userId) // ✅ Using the user ID as the document name
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "✅ User data saved successfully for ID: $userId")
            }
            .addOnFailureListener { error ->
                Log.e("Firestore", "❌ Firestore permission error: ${error.message}")
            }
    }

    fun getUserFromFirestore(userId: String?, onResult: (Map<String, String>?) -> Unit) {
        if (userId.isNullOrEmpty()) {
            Log.e("Firestore", "Invalid user ID: $userId")
            onResult(null)
            return
        }

        Log.d("Firestore", "Fetching user profile from Firestore for ID: $userId")
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = mutableMapOf<String, String>()
                    document.getString("user_id")?.let { userData["user_id"] = it }
                    document.getString("name")?.let { userData["name"] = it }
                    document.getString("email")?.let { userData["email"] = it }
                    document.getString("avatar_url")?.let { userData["avatar_url"] = it }

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

    fun testFirestoreConnection() {
        firestore.collection("test").document("connection").get()
            .addOnSuccessListener {
                Log.d("Firestore", "Test document fetched: $it")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Firestore connection error: ${it.message}")
            }
    }





}