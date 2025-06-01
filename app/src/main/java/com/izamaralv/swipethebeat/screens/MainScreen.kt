package com.izamaralv.swipethebeat.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.navigation.Screen
import com.izamaralv.swipethebeat.repository.SongRepository
import com.izamaralv.swipethebeat.ui.components.NotificationHelper
import com.izamaralv.swipethebeat.ui.components.STBTopAppBar
import com.izamaralv.swipethebeat.ui.components.TinderCard
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, profileViewModel: ProfileViewModel, songViewModel: SongViewModel) {


    val displayName = profileViewModel.getDisplayName()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile(profileViewModel.getUserId()) // ✅ Load Firestore color
    }


    // Control de la barra de estado del sistema
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = softComponentColor.value, darkIcons = false)

    val songRepository = SongRepository(context)

    // Obtener el token de acceso dinámicamente
    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken()

    accessToken?.let {
        Log.d("MainScreen", "Access Token: $it")
    }

    LaunchedEffect(accessToken) {
        accessToken?.let {
            val userProfile = songRepository.getCurrentUserProfile(it)
            userProfile?.let { profile ->
                Log.d("MainScreen", "User Profile: $profile")
            }
        }
    }

    Scaffold(
        topBar = {
            STBTopAppBar(
                profileViewModel,
                navController = navController,
                customIcon = Icons.Filled.Favorite,
                customFunction = { navController.navigate(Screen.LikedSongs.route) },
                customText = "Últimos likes"
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val currentSong by songViewModel.currentSong.observeAsState()

            currentSong?.let { song ->
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bienvenido/a $displayName",
                            color = softComponentColor.value,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Image(
                                painter = painterResource(R.drawable.red_arrow),
                                contentDescription = "Red Arrow",
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(top = 10.dp)
                            )

                            Button(
                                onClick = { /* TODO */ },
                                colors = ButtonDefaults.buttonColors(containerColor = softComponentColor.value),
                            ) {
                                Text(text = "Guardar", color = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp)) // ✅ Spacing between text & icon
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = "Save for later",
                                    tint = Color.Black
                                )
                            }

                            Image(
                                painter = painterResource(R.drawable.green_arrow),
                                contentDescription = "Green Arrow",
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(top = 10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TinderCard(
                            onSwipeLeft = { songViewModel.dislikeCurrentSong() },
                            onSwipeRight = { songViewModel.likeCurrentSong() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.75f)
                                    .background(cardColor.value, shape = RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {


                                    Spacer(modifier = Modifier.height(20.dp))

                                    Image(
                                        painter = rememberAsyncImagePainter(song.album.images.first().url),
                                        contentDescription = song.name,
                                        modifier = Modifier
                                            .size(200.dp)
                                            .padding(8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = song.name,
                                        style = TextStyle(
                                            color = softComponentColor.value,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = song.artists.joinToString(", ") { it.name },
                                        style = TextStyle(
                                            color = softComponentColor.value,
                                            fontSize = 18.sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = song.album.name,
                                        style = TextStyle(
                                            color = softComponentColor.value,
                                            fontSize = 18.sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón para reproducir en Spotify
                        IconButton(
                            onClick = {
                                val spotifyUri = "spotify:track:${song.id}"
                                val intent = Intent(Intent.ACTION_VIEW, spotifyUri.toUri())

                                intent.putExtra(
                                    Intent.EXTRA_REFERRER,
                                    "android-app://${context.packageName}".toUri()
                                )
                                NotificationHelper.showPersistentNotification(context)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .size(60.dp) // ✅ Keeps a slightly larger button for the circular background
                                .background(
                                    softComponentColor.value,
                                    shape = CircleShape
                                ) // ✅ Adds round background (placeholder color)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.Black, // ✅ Makes the icon stand out against the background
                                modifier = Modifier.size(40.dp)
                            )
                        }


                    }
                }
            } ?: run {
                Text(
                    text = "Cargando...",
                    style = TextStyle(color = softComponentColor.value),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
