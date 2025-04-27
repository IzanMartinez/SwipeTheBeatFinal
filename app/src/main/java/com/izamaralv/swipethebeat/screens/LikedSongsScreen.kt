package com.izamaralv.swipethebeat.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import com.izamaralv.swipethebeat.ui.components.STBTopAppBar
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModelFactory

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LikedSongsScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
) {
    // Controlador para el sistema de UI
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = softComponentColor.value, darkIcons = false)

    val context = LocalContext.current
    val songRepository = SongRepository(context)

    // Obtiene el token de acceso de manera dinámica
    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken()

    // Inicializa el ViewModel de canciones
    val songViewModel: SongViewModel = viewModel(
        factory = SongViewModelFactory(songRepository, accessToken ?: "")
    )

    // Observa la lista de canciones favoritas
    val likedSongsPool by songViewModel.likedSongsPool.observeAsState(emptyList())

    // Obtiene las últimas 50 canciones o todas si son menos de 50
    val songsToShow = if (likedSongsPool.size > 50) {
        likedSongsPool.takeLast(50)
    } else {
        likedSongsPool
    }

    Scaffold(
        topBar = {
            STBTopAppBar(
                profileViewModel,
                onLogout = { navController.navigate("login_screen") },
                firstIcon = Icons.Filled.Home,
                firstFunction = { navController.navigate("main_screen") },
                firstOption = "Inicio"
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Liked Songs",
                style = TextStyle(color = softComponentColor.value, fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn {
                items(songsToShow) { song ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(color = cardColor.value, shape = RoundedCornerShape(16.dp))
                            .clickable {
                                val spotifyUri = "spotify:track:${song.id}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUri))
                                intent.putExtra(
                                    Intent.EXTRA_REFERRER,
                                    Uri.parse("android-app://${context.packageName}")
                                )
                                context.startActivity(intent)
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(song.album.images.first().url),
                                contentDescription = song.name,
                                modifier = Modifier.size(64.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = song.name,
                                    style = TextStyle(
                                        color = softComponentColor.value,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = song.artists.joinToString(", ") { it.name },
                                    style = TextStyle(
                                        color = softComponentColor.value,
                                        fontSize = 14.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = {
                                    songViewModel.dislikeCurrentSong(song.id) // Función para marcar la canción como no favorita
                                }
                            ) {
                                Text(text = "✖", color = Color.Red.copy(.6f))
                            }
                        }
                    }
                }
            }
        }
    }
}
