package com.izamaralv.swipethebeat.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email.copy(), password.copy())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {                                                                                                                                                                                                                                                 
                    errorMessage = task.exception?.message ?: "Login failed"
                }
            }
    }
}