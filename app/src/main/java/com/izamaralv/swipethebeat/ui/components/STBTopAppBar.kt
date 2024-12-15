package com.izamaralv.swipethebeat.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.izamaralv.swipethebeat.common.backgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun STBTopAppBar(profileViewModel: ProfileViewModel, onLogout: () -> Unit) {
    val profileImageUrl by profileViewModel.profileImageUrl.observeAsState()

    // Log the profile image URL
    Log.d("STBTopAppBar", "Profile image URL: $profileImageUrl")

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
//                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu Icon")
            }
        },
        title = {
            Image(
                painter = painterResource(id = R.drawable.logo2recolor1krita_color2),
                contentDescription = "App logo",
                modifier = Modifier.size(135.dp)
            )
        },
        actions = {
            profileImageUrl?.let { url ->
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build()
                )
                Log.d("STBTopAppBar", "Loading profile image from URL: $url")
                Image(
                    painter = painter,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, backgroundColor, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            IconButton(onClick = onLogout) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout Icon")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = softComponentColor,
        )
    )
}

