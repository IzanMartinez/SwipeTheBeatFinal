package com.izamaralv.swipethebeat.screens

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.cardBorderColor
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.common.textColor
import com.izamaralv.swipethebeat.models.Track
import com.izamaralv.swipethebeat.navigation.Screen
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
    // Ajuste del color de la barra de estado
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = softComponentColor.value, darkIcons = false)

    val context = LocalContext.current
    val songRepository = SongRepository()
    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken()

    // Creamos el ViewModel para manejar las canciones favoritas
    val songViewModel: SongViewModel = viewModel(
        factory = SongViewModelFactory(songRepository, accessToken ?: "")
    )

    // Lista de canciones favoritas
    val likedSongsPool by songViewModel.likedSongsPool.observeAsState(emptyList())

    // Control para mostrar u ocultar la barra de búsqueda
    var searchActive by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    // Filtramos la lista según la búsqueda
    val songsToShow: List<Track> = if (query.isBlank()) {
        if (likedSongsPool.size > 50) likedSongsPool.takeLast(50)
        else likedSongsPool
    } else {
        likedSongsPool.filter { track ->
            val matchesName   = track.name.contains(query, ignoreCase = true)
            val matchesArtist = track.artists.any { it.name.contains(query, ignoreCase = true) }
            matchesName || matchesArtist
        }
    }

    Scaffold(
        topBar = {
            // ─── Misma topBar que facilitaste ───
            STBTopAppBar(
                profileViewModel,
                navController = navController,
                customIcon1    = Icons.Filled.Audiotrack,
                customFunction1= { navController.navigate(Screen.Main.route) },
                customText1    = "Pantalla principal",
                customIcon2    = Icons.Filled.AccessTime,
                customFunction2= { navController.navigate(Screen.SavedSongs.route) },
                customText2    = "Ver más tarde"
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Encabezado con título y botón de búsqueda
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text      = "Canciones que te gustan",
                    style     = TextStyle(
                        color     = softComponentColor.value,
                        fontSize  = 20.sp,
                        fontWeight= FontWeight.Bold
                    ),
                    modifier  = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                IconButton(onClick = { searchActive = !searchActive }) {
                    Icon(
                        imageVector       = Icons.Filled.Search,
                        contentDescription= "Buscar",
                        tint              = softComponentColor.value
                    )
                }
            }

            // Campo de búsqueda
            if (searchActive) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            text  = "Filtrar por nombre o artista...",
                            color = softComponentColor.value.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,

                    colors = TextFieldDefaults.colors(
                        focusedTextColor         = textColor.value,
                        unfocusedTextColor       = textColor.value,
                        focusedContainerColor    = cardColor.value,
                        unfocusedContainerColor  = cardColor.value,
                        cursorColor              = softComponentColor.value,
                        focusedIndicatorColor    = softComponentColor.value,
                        unfocusedIndicatorColor  = textColor.value.copy(alpha = 0.4f),
                        focusedPlaceholderColor  = textColor.value.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor= textColor.value.copy(alpha = 0.6f)
                    ),

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Lista de canciones
            LazyColumn {
                items(songsToShow) { song ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(color = cardColor.value, shape = RoundedCornerShape(16.dp))
                            .border(1.dp, cardBorderColor.value, shape = RoundedCornerShape(16.dp))
                            .clickable {
                                // Abrir en Spotify al pulsar
                                val spotifyUri = "spotify:track:${song.id}"
                                val intent = Intent(Intent.ACTION_VIEW, spotifyUri.toUri())
                                intent.putExtra(
                                    Intent.EXTRA_REFERRER,
                                    "android-app://${context.packageName}".toUri()
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
                                painter           = rememberAsyncImagePainter(song.album.images.first().url),
                                contentDescription= song.name,
                                modifier          = Modifier.size(64.dp),
                                contentScale      = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text      = song.name,
                                    style     = TextStyle(
                                        color      = softComponentColor.value,
                                        fontSize   = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines  = 1,
                                    overflow  = TextOverflow.Ellipsis
                                )
                                Text(
                                    text      = song.artists.joinToString(", ") { it.name },
                                    style     = TextStyle(
                                        color    = softComponentColor.value,
                                        fontSize = 14.sp
                                    ),
                                    maxLines  = 1,
                                    overflow  = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            // Botón para quitar de favoritos en Spotify
                            IconButton(onClick = {
                                songViewModel.dislikeCurrentSong(song.id)
                            }) {
                                Text(text = "✖", color = Color.Red.copy(alpha = .6f))
                            }
                        }
                    }
                }
            }
        }
    }
}
