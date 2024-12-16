package com.izamaralv.swipethebeat.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.repository.SongRepository
import com.izamaralv.swipethebeat.ui.components.STBTopAppBar
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModelFactory
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import com.izamaralv.swipethebeat.common.cardColor
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import com.izamaralv.swipethebeat.ui.components.TinderCard
import com.izamaralv.swipethebeat.utils.Credentials.CLIENT_ID
import com.izamaralv.swipethebeat.utils.Credentials.REDIRECT_URI
import com.izamaralv.swipethebeat.utils.SpotifyConnection
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, profileViewModel: ProfileViewModel) {
    val displayName by profileViewModel.displayName.observeAsState()
    val profileImageUrl by profileViewModel.profileImageUrl.observeAsState()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val songRepository = SongRepository(context)

    // Media Player
    var spotifyAppRemote: SpotifyAppRemote? = null
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }


    // Retrieve the access token dynamically
    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken()

    accessToken?.let {
        Log.d("MainScreen", "Access Token: $it")
    }

    val songViewModel: SongViewModel = viewModel(
        factory = SongViewModelFactory(songRepository, accessToken ?: "")
    )

    LaunchedEffect(Unit) {
        SpotifyConnection.connectToSpotify(
            context = context,
            clientId = CLIENT_ID,  // Replace with your actual client ID
            redirectUri = REDIRECT_URI,  // Match your manifest configuration
            onConnected = { appRemote ->
                spotifyAppRemote = appRemote
            },
            onError = { throwable ->
                Log.e("MainScreen", "Error connecting to Spotify: ${throwable.message}")
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            SpotifyConnection.disconnectSpotify()
        }
    }


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
                onLogout = { navController.navigate("login_screen") })
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
                        .fillMaxSize()
//                        .padding(16.dp),
                    , contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                                    )
                                    {
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
                        // play button
                        Button(
                            modifier = Modifier
                                .padding(top = 50.dp)
                                .height(60.dp)
                                .width(200.dp),

                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("spotify:track:${song.id}") // Use the Spotify URI for the song
                                )
                                intent.putExtra(
                                    Intent.EXTRA_REFERRER,
                                    Uri.parse("android-app://${context.packageName}")
                                )
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


