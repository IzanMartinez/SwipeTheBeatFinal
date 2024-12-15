package com.izamaralv.swipethebeat.screens

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, profileViewModel: ProfileViewModel) {
    val displayName by profileViewModel.displayName.observeAsState()
    val profileImageUrl by profileViewModel.profileImageUrl.observeAsState()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val songRepository = SongRepository(context)

    // Media Player State
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
                .background(color = backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currentSong by songViewModel.currentSong.observeAsState()

            currentSong?.let { song ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TinderSwipeCard(
                        onSwipeLeft = { songViewModel.dislikeCurrentSong() },
                        onSwipeRight = { songViewModel.likeCurrentSong() }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.75f)
                                .background(cardColor, shape = RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(song.album.images.first().url),
                                    contentDescription = song.name,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .padding(8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = song.name,
                                    style = TextStyle(
                                        color = softComponentColor,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = song.artists.joinToString(", ") { it.name },
                                    style = TextStyle(color = softComponentColor, fontSize = 18.sp),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = song.album.name,
                                    style = TextStyle(color = softComponentColor, fontSize = 18.sp),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // play button
                                Button(
                                    onClick = {
                                        if (isPlaying) {
                                            mediaPlayer?.stop()
                                            mediaPlayer?.release()
                                            mediaPlayer = null
                                            isPlaying = false
                                        } else {
                                            mediaPlayer = MediaPlayer().apply {
                                                setDataSource(song.preview_url)
                                                prepare()
                                                start()
                                            }
                                            isPlaying = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPlaying) Color.Red else Color.Green
                                    )
                                ) {
                                    Text(text = if (isPlaying) "Stop" else "Play")
                                }

                            }
                        }
                    }
                }
            } ?: run {
                Text(
                    text = "No song available",
                    style = TextStyle(color = softComponentColor),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TinderSwipeCard(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Dynamic overlay color and opacity
    val overlayColor = when {
        offsetX > 0 -> Color.Green.copy(alpha = minOf(0.4f, offsetX / 600))
        offsetX < 0 -> Color.Red.copy(alpha = minOf(0.4f, -offsetX / 600))
        else -> Color.Transparent
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth(.8f)
            .fillMaxHeight(.55f)
//            .aspectRatio(0.75f) // Slimmer card aspect ratio
            .graphicsLayer(
                translationX = offsetX,
                translationY = offsetY,
                rotationZ = offsetX / 20
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        when {
                            offsetX > 300 -> onSwipeRight()
                            offsetX < -300 -> onSwipeLeft()
                        }
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            }
    ) {
        // Overlay for swipe feedback (always on top)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f) // Ensures overlay is above the card content
                .background(overlayColor, shape = RoundedCornerShape(16.dp))
        )

        // Card content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f) // Places card content below the overlay
                .background(cardColor, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}
