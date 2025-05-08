package com.izamaralv.swipethebeat.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.izamaralv.swipethebeat.models.User
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    var userIncorr = false

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Showboxd", "signInWithEmailAndPassword logged!!")
                            home()
                        } else {
                            val exception = task.exception
                            if (exception is FirebaseAuthInvalidCredentialsException) {
                                // Mostrar diálogo de error
                                userIncorr = true
                                // Manejar credenciales incorrectas
                                Log.d("Showboxd", "Credenciales incorrectas")
                                // Mostrar un mensaje de error al usuario
                                _loading.value = false
                                // Otras acciones para manejar el error
                            } else {
                                // Manejar otros errores
                                Log.d("Showboxd", "Error al iniciar sesión: ${exception?.message}")
                            }
                        }
                    }
            } catch (ex: Exception) {
                Log.d("Showboxd", "signInWithEmailAndPassword ${ex.message}")
                // Manejar otros errores
            }
        }


    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        home: () -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val nombre = nombre
                        val email = email
                        val apellido = apellido
                        createUser(nombre, apellido, email)
                        home()
                    } else {
                        Log.d(
                            "Showboxd",
                            "createUserWithEmailAndPassword: ${task.result.toString()}"
                        )
                    }
                    _loading.value = false
                }
        }
    }


    private fun createUser(nombre: String?, apellido: String?, email: String?) {
        val userId = auth.currentUser?.uid

        //User data class
        val user = User(
            user_id = userId.toString(),
            email = email.toString(),
            nombre = nombre.toString(),
            admin = false,
            spotify_token = ""
        ).toMap()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
            .addOnSuccessListener {
                Log.d("Showboxd", "${it.id} created")
            }.addOnFailureListener {
                Log.d("Showboxd", "Ocurrio un error")
            }
    }
}
