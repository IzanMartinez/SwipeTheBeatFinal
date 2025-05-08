package com.izamaralv.swipethebeat.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore

class ProfileManagerFirebase {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun getUserData(userId: String, onComplete: (Map<String, Any>?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onComplete(document.data)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileManagerFirebase", "Error getting user data: ", exception)
                onComplete(null)
            }
    }
}