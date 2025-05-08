package com.izamaralv.swipethebeat.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileManagerFirebase {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun getUserById(userId: String): FirebaseUser? {
        return if (firebaseAuth.currentUser?.uid == userId) {
            firebaseAuth.currentUser
        } else {
            null
        }
    }
}