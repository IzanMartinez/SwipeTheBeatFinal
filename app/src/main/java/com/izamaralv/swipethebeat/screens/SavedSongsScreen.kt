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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun SavedSongsScreen(
    navController: NavHostController, profileViewModel: ProfileViewModel
) {
    // (1) Cargar las guardadas al montar la pantalla
    LaunchedEffect(Unit) {
        profileViewModel.loadSavedSongsToState()
    }

    // (2) Estado observable de la lista de guardadas
    val savedSongs = profileViewModel.savedSongs

    // (3) Estado para controlar la búsqueda
    var searchActive by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    // (4) Filtrar según nombre de canción o artista
    val filteredSongs: List<Track> = if (query.isBlank()) {
        savedSongs
    } else {
        savedSongs.filter { track ->
            val inName = track.name.contains(query, ignoreCase = true)
            val inArtist = track.artists.any { it.name.contains(query, ignoreCase = true) }
            inName || inArtist
        }
    }

    // (5) Ajuste de la barra de estado
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = softComponentColor.value, darkIcons = false)

    val context = LocalContext.current
    val songRepository = SongRepository(context)
    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken() ?: ""
    val songViewModel: SongViewModel = viewModel(
        factory = SongViewModelFactory(songRepository, accessToken)
    )

    // (6) Obtener nombre de usuario para saludo
    val displayName = profileViewModel.getDisplayName()

    Scaffold(
        topBar = {
            STBTopAppBar(
                profileViewModel = profileViewModel,
                navController = navController,
                customIcon1 = Icons.Filled.Favorite,
                customFunction1 = { navController.navigate(Screen.LikedSongs.route) },
                customText1 = "Últimos likes",
                customIcon2 = Icons.Filled.Audiotrack,
                customFunction2 = { navController.navigate(Screen.Main.route) },
                customText2 = "Pantalla principal",
            )
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // (7) Título con búsqueda en la misma fila
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Canciones guardadas", style = TextStyle(
                        color = softComponentColor.value,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ), modifier = Modifier.weight(1f), textAlign = TextAlign.Start
                )
                IconButton(onClick = { searchActive = !searchActive }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Buscar",
                        tint = softComponentColor.value
                    )
                }
            }

            // (8) Si está activo, mostrar el TextField debajo del título
            if (searchActive) {
                TextField(
                    value = query, onValueChange = { query = it }, placeholder = {
                    Text(
                        text = "Filtrar por nombre o artista...",
                        color = textColor.value.copy(alpha = 0.6f)
                    )
                }, singleLine = true,

                    colors = TextFieldDefaults.colors(
                        // Color del texto cuando está enfocado / desenfocado
                        focusedTextColor = textColor.value,
                        unfocusedTextColor = textColor.value,

                        // Color del fondo (container) siempre igual aquí
                        focusedContainerColor = cardColor.value,
                        unfocusedContainerColor = cardColor.value,

                        // Color del cursor
                        cursorColor = softComponentColor.value,

                        // Color de la línea inferior cuando está enfocado / desenfocado
                        focusedIndicatorColor = softComponentColor.value,
                        unfocusedIndicatorColor = textColor.value.copy(alpha = 0.4f),

                        // Color del placeholder (texto gris cuando no escribes)
                        focusedPlaceholderColor = textColor.value.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = textColor.value.copy(alpha = 0.6f)
                    ),

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            // (9) Lista en LazyColumn usando filteredSongs
            if (filteredSongs.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredSongs) { song ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(
                                    color = cardColor.value, shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    1.dp, cardBorderColor.value, shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    // Al tocar la fila, abrimos Spotify
                                    val spotifyUri = "spotify:track:${song.id}"
                                    val intent = Intent(Intent.ACTION_VIEW, spotifyUri.toUri())
                                    intent.putExtra(
                                        Intent.EXTRA_REFERRER,
                                        "android-app://${context.packageName}".toUri()
                                    )
                                    context.startActivity(intent)
                                }
                                .padding(12.dp)) {
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
                                        text = song.name, style = TextStyle(
                                            color = softComponentColor.value,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        ), maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = song.artists.joinToString(", ") { it.name },
                                        style = TextStyle(
                                            color = softComponentColor.value, fontSize = 14.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // (9.1) Botón “Eliminar” de Firebase + lista local
                                IconButton(onClick = {
                                    val idx = profileViewModel.savedSongs.indexOf(song)
                                    if (idx != -1) {
                                        profileViewModel.deleteSavedSongAt(idx)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Eliminar",
                                        tint = softComponentColor.value

                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // (9.2) Botón “Like” enviándolo a Spotify y eliminando
                                IconButton(onClick = {
                                    val idx = profileViewModel.savedSongs.indexOf(song)
                                    if (idx != -1) {
                                        songViewModel.likeSongById(song.id)
                                        profileViewModel.deleteSavedSongAt(idx)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = "Like",
                                        tint = softComponentColor.value
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // (10) Lista vacía o sin coincidencias de búsqueda
                Text(
                    text = if (query.isBlank()) "No has guardado canciones para más tarde"
                    else "No se encontraron coincidencias para \"$query\"", style = TextStyle(
                        color = textColor.value, fontSize = 18.sp
                    ), modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center
                )
            }
        }
    }
}

