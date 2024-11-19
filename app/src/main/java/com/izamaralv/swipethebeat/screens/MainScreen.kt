package com.izamaralv.swipethebeat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.ui.components.STBTopAppBar
import com.izamaralv.swipethebeat.viewmodel.ProfileVIewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, profileViewModel: ProfileVIewModel, songViewModel: SongViewModel) {
    val displayName = profileViewModel.displayName.observeAsState()
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
                .background(color = backgroundColor)
                //padding for the top bar
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            songViewModel.recommendedSongs.value?.let { songs ->
                for (song in songs) {
                    Text(
                        text = "${song.name} - ${song.artists.joinToString(", ")} - ${song.albumName}",
                        style = TextStyle(color = softComponentColor),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } ?: run {
                Text(
                    text = "No recommendations available",
                    style = TextStyle(color = softComponentColor),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }


}

