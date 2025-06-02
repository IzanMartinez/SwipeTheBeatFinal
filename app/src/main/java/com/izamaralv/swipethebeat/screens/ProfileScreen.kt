package com.izamaralv.swipethebeat.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.cardBorderColor
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.common.textColor
import com.izamaralv.swipethebeat.navigation.Screen
import com.izamaralv.swipethebeat.ui.components.ColorPickerMenu
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel
) {
    // ▶ Apply status bar theming
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = backgroundColor.value, darkIcons = false)

    val context = LocalContext.current

    // ▶ Trigger loading user data (including favorite artists) once per composition
    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile(profileViewModel.getUserId())
    }

    // Profile image setup
    val profileImageUrl = profileViewModel.getProfileImageUrl()
    val painter = if (profileImageUrl.isEmpty()) {
        painterResource(id = R.drawable.default_profile)
    } else {
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(profileImageUrl)
                .crossfade(true)
                .build()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.value)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            // Back button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(60.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = softComponentColor.value
                ),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = softComponentColor.value,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Profile image centered
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape)
                        .border(5.dp, softComponentColor.value, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Card container
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
                .height(470.dp)
                .background(color = cardColor.value, shape = RoundedCornerShape(16.dp))
                .border(5.dp, cardBorderColor.value, RoundedCornerShape(16.dp))
        ) {
            // Username section
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.height(100.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Nombre de usuario:",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor.value
                    )
                )
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = profileViewModel.getDisplayName(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = softComponentColor.value
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ▶ Header for favorite-artist pickers
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = "Elige tus artistas favoritos:",
                style = TextStyle(
                    color = textColor.value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ▶ Read favorite artists from ViewModel instead of savedStateHandle
            val fav1 = profileViewModel.favoriteArtist1.ifBlank { "Artista favorito #1" }
            val fav2 = profileViewModel.favoriteArtist2.ifBlank { "Artista favorito #2" }
            val fav3 = profileViewModel.favoriteArtist3.ifBlank { "Artista favorito #3" }

            // Button #1
            OutlinedButton(
                onClick = { navController.navigate(Screen.ArtistPicker1.route) },
                shape = RoundedCornerShape(50),
                border = BorderStroke(2.dp, softComponentColor.value),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = cardColor.value,
                    contentColor = textColor.value
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(fav1)
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button #2
            OutlinedButton(
                onClick = { navController.navigate(Screen.ArtistPicker2.route) },
                shape = RoundedCornerShape(50),
                border = BorderStroke(2.dp, softComponentColor.value),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = cardColor.value,
                    contentColor = textColor.value
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(fav2)
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button #3
            OutlinedButton(
                onClick = { navController.navigate(Screen.ArtistPicker3.route) },
                shape = RoundedCornerShape(50),
                border = BorderStroke(2.dp, softComponentColor.value),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = cardColor.value,
                    contentColor = textColor.value
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(fav3)
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Color picker (existing)
            ColorPickerMenu(profileViewModel)
        }

        // Logout button (existing)
        Box(
            modifier = Modifier
                .padding(top = 40.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = { navController.navigate(Screen.Login.route) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text(
                    text = "Cerrar sesión",
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
