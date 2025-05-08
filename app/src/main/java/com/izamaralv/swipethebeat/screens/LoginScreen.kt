package com.izamaralv.swipethebeat.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.Task
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.ui.components.HelpLink
import com.izamaralv.swipethebeat.ui.theme.greenPastelColor
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

@Composable
fun LoginScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    // Control de la barra de estado del sistema
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = backgroundColor.value, darkIcons = false)

    // Layout general de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.value).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(top = 60.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.border_logo_green),
                contentDescription = "Logo", modifier = Modifier.size(200.dp)
            )
            // Título
            Text(
                text = "Swipe The Beat",
                color = greenPastelColor,
                textAlign = TextAlign.Center,
                lineHeight = 3.5.em, style = androidx.compose.ui.text.TextStyle(
                    fontSize = 40.sp
                ),
                modifier = Modifier
                    .padding(top = 8.dp) // Espaciado entre el logo y el título
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(1.dp)) // Espacio entre el título y el botón
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task: Task<AuthResult> ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Firebase Auth", "signInWithEmail:success")
                                val user = auth.currentUser
                                errorMessage = null
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Firebase Auth", "signInWithEmail:failure", task.exception)
                                errorMessage = "Authentication failed."
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            // Display error message if there is one
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        // Composable para el enlace de ayuda
        HelpLink("¿Necesitas ayuda? Envia un correo a soporte")
    }
}

