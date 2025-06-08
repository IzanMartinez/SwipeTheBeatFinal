package com.izamaralv.swipethebeat.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.navigation.Screen
import com.izamaralv.swipethebeat.utils.changeColor
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

@Composable
fun LobbyScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel
) {
    // Ajuste del color de la barra de estado
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = backgroundColor.value, darkIcons = false)

    // Cambio de color de la aplicación al acceder a la pantalla
    changeColor(softComponentColor.value, userId = profileViewModel.getUserId(), profileViewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.value),
        verticalArrangement   = Arrangement.spacedBy(16.dp),
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(top = 16.dp))

        // Card 1: Recomendaciones
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .weight(1f)
                .clickable { navController.navigate(Screen.Main.route) }
                .background(color = cardColor.value, shape = RoundedCornerShape(16.dp))
                .border(5.dp, softComponentColor.value, shape = RoundedCornerShape(16.dp))
            ,
            colors    = CardDefaults.cardColors(containerColor = cardColor.value),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Audiotrack,
                        contentDescription = "Ícono Recomendaciones",
                        tint = softComponentColor.value,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    )
                    Text(
                        text      = "Recomendaciones",
                        color     = softComponentColor.value,
                        fontSize  = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Card 2: Perfil
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .weight(1f)
                .clickable { navController.navigate(Screen.Profile.route) }
                .background(color = cardColor.value, shape = RoundedCornerShape(16.dp))
                .border(5.dp, softComponentColor.value, shape = RoundedCornerShape(16.dp))
            ,
            colors    = CardDefaults.cardColors(containerColor = cardColor.value),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Ícono Perfil",
                        tint = softComponentColor.value,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    )
                    Text(
                        text      = "Perfil",
                        color     = softComponentColor.value,
                        fontSize  = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Card 3: Canciones que te gustan
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .weight(1f)
                .clickable { navController.navigate(Screen.LikedSongs.route) }
                .background(color = cardColor.value, shape = RoundedCornerShape(16.dp))
                .border(5.dp, softComponentColor.value, shape = RoundedCornerShape(16.dp))
            ,
            colors    = CardDefaults.cardColors(containerColor = cardColor.value),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Ícono Canciones que te gustan",
                        tint = softComponentColor.value,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    )
                    Text(
                        text      = "Canciones que te gustan",
                        color     = softComponentColor.value,
                        fontSize  = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Card 4: Canciones guardadas para más tarde
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .weight(1f)
                .clickable { navController.navigate(Screen.SavedSongs.route) }
                .background(color = cardColor.value, shape = RoundedCornerShape(16.dp))
                .border(5.dp, softComponentColor.value, shape = RoundedCornerShape(16.dp))
            ,
            colors    = CardDefaults.cardColors(containerColor = cardColor.value),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),

        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = "Ícono Canciones guardadas",
                        tint = softComponentColor.value,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    )
                    Text(
                        text      = "Canciones guardadas para más tarde",
                        color     = softComponentColor.value,
                        fontSize  = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(bottom = 16.dp))
    }
}
