package com.izamaralv.swipethebeat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.navigation.Screen
import com.izamaralv.swipethebeat.ui.components.RecommendationsSection
import com.izamaralv.swipethebeat.ui.components.STBTopAppBar
import com.izamaralv.swipethebeat.ui.components.WelcomeHeader
import com.izamaralv.swipethebeat.viewmodel.GeminiRecommendationViewModel
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    geminiViewModel: GeminiRecommendationViewModel
) {
    val displayName = profileViewModel.getDisplayName()
    val state by geminiViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile(profileViewModel.getUserId())
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(softComponentColor.value, darkIcons = false)

    Scaffold(
        topBar = {
            STBTopAppBar(
                profileViewModel,
                navController = navController,
                customIcon1 = Icons.Filled.Favorite,
                customFunction1 = { navController.navigate(Screen.LikedSongs.route) },
                customText1 = "Últimos likes",
                customIcon2 = Icons.Filled.AccessTime,
                customFunction2 = { navController.navigate(Screen.SavedSongs.route) },
                customText2 = "Ver más tarde"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor.value)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // -- Cabecera de bienvenida fuera de la tarjeta --
            WelcomeHeader(displayName)

            Spacer(modifier = Modifier.height(40.dp))

            RecommendationsSection(
                state = state,
                onSave = {
                    // 1) Guarda en Firestore
                    val current =
                        (state as GeminiRecommendationViewModel.RecommendationState.Success)
                            .recommendations.first()
                    profileViewModel.saveSongForLater(current)
                    // 2) Descarta la carta
                    geminiViewModel.onSongSwiped()
                },
                onLike = { track ->
                    geminiViewModel.likeSongInSpotify(track.id)
                    geminiViewModel.onSongSwiped()
                },
                onDislike = {
                    geminiViewModel.onSongSwiped()
                }
            )
        }
    }
}


