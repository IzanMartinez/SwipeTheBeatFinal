package com.izamaralv.swipethebeat.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.repository.SongRepository
import com.izamaralv.swipethebeat.ui.components.NotificationHelper
import com.izamaralv.swipethebeat.ui.components.STBTopAppBar
import com.izamaralv.swipethebeat.ui.components.TinderCard
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModelFactory
import androidx.core.net.toUri

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, profileViewModel: ProfileViewModel) {
    // Control de la barra de estado del sistema
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = softComponentColor.value, darkIcons = false)

    val displayName by profileViewModel.displayName.observeAsState()

    val context = LocalContext.current
    val songRepository = SongRepository(context)

    // Obtener el token de acceso dinámicamente
    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken()

    accessToken?.let {
        Log.d("MainScreen", "Access Token: $it")
    }

    val songViewModel: SongViewModel = viewModel(
        factory = SongViewModelFactory(songRepository, accessToken ?: "")
    )

    LaunchedEffect(accessToken) {
        accessToken?.let {
            val userProfile = songRepository.getCurrentUserProfile(it)
            userProfile?.let { profile ->
                Log.d("MainScreen", "User Profile: $profile")
            }
        }
        songViewModel.loadInitialRecommendations()
    }

    Scaffold(
        topBar = {
            STBTopAppBar(
                profileViewModel,
                onLogout = { navController.navigate("login_screen") },
                firstIcon = Icons.Filled.Favorite,
                firstFunction = { navController.navigate("liked_songs_screen") },
                firstOption = "Últimos likes"
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
                            text = "Bienvenido/a ${displayName ?: "Invitado"}",
                            color = softComponentColor.value
                        )

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
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(.9f)
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "✖", color = Color.Red.copy(.6f))
                                        Text(text = "✔", color = Color.Green.copy(.6f))
                                    }

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
                        Button(
//                            modifier = Modifier.size(),
                            onClick = {
                                val spotifyUri = "spotify:track:${song.id}"
                                val intent = Intent(Intent.ACTION_VIEW, spotifyUri.toUri())

                                // Agregar referrer opcional
                                intent.putExtra(
                                    Intent.EXTRA_REFERRER,
                                    "android-app://${context.packageName}".toUri()
                                )
                                NotificationHelper.showPersistentNotification(context)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = softComponentColor.value)
                        ) {
                            Text(text = "Play in Spotify", fontWeight = FontWeight.ExtraBold)
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
