package com.izamaralv.swipethebeat.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavHostController, profileViewModel: ProfileViewModel) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = softComponentColor.value, darkIcons = false)

    val profileImageUrl = profileViewModel.getProfileImageUrl()
    val displayName = profileViewModel.getDisplayName()


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

    }

}