package com.izamaralv.swipethebeat.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.ui.components.HelpLink
import com.izamaralv.swipethebeat.ui.theme.greenPastelColor
import com.izamaralv.swipethebeat.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // Control de la barra de estado del sistema
    val email = viewModel.email.value
    val password = viewModel.password.value
    val errorMessage = viewModel.errorMessage.value
    val onEmailChange = { newEmail: String -> viewModel.onEmailChange(newEmail) }
    val onPasswordChange = { newPassword: String -> viewModel.onPasswordChange(newPassword) }
    val onLoginClick = { viewModel.login(navController)}



    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = backgroundColor.value, darkIcons = false)

    val context = LocalContext.current
    // Layout general de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.value),
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
                contentDescription = "Logo",
                modifier = Modifier
                    .requiredWidth(412.dp)
                    .requiredHeight(386.dp)
            )
            // Título
            Text(
                text = "Swipe The Beat",
                color = greenPastelColor,
                textAlign = TextAlign.Center,
                lineHeight = 3.5.em,
                style = TextStyle(
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
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Correo electrónico",
                modifier = Modifier
                    .padding(bottom = 10.dp, start = 10.dp)
                    .fillMaxWidth(.7f),
                color = Color.White,
                style = TextStyle(
                    fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
            )

            TextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = {  },
            modifier = Modifier
                    .fillMaxWidth(.7f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(20.dp)) // Adds a white border
                    .background(cardColor.value), // Keeps the background color
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = cardColor.value, // Ensures full background color
                    focusedContainerColor = cardColor.value,
                    unfocusedIndicatorColor = Color.Transparent, // Removes blue underline
                    focusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.White),
                singleLine = true
            )


            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Contraseña",
                modifier = Modifier
                    .padding(bottom = 10.dp, start = 10.dp)
                    .fillMaxWidth(.7f),
                color = Color.White,
                style = TextStyle(
                    fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
            )

            TextField(

                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier
                    .fillMaxWidth(.7f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(20.dp)) // Adds a white border
                    .background(cardColor.value), // Maintains the background change
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = cardColor.value, // Ensures full background color
                    focusedContainerColor = cardColor.value,
                    unfocusedIndicatorColor = Color.Transparent, // Removes blue underline
                    focusedIndicatorColor = Color.Transparent
                ),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.White),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                                    .fillMaxWidth(.7f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable(onClick = onLoginClick)


                                    }
                                    .background(color = greenPastelColor)
                                    .padding(horizontal = 25.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "Inicia sesión",
                                    color = Color.Black,
                                    lineHeight = 8.75.em,
                                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold) // Added bold styling
                                )
                            }
                            if (errorMessage != null) {
                                Text(text = errorMessage, color = Color.Red)
                            }
            


            Spacer(modifier = Modifier.height(40.dp)) // Espacio entre el botón y el enlace de texto

            // Composable para el enlace de ayuda
            Row(modifier = modifier) {
                HelpLink("¿Necesitas ayuda? Envia un correo a soporte")
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

