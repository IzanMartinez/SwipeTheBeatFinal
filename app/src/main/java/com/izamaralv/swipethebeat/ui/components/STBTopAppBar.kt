package com.izamaralv.swipethebeat.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.viewmodel.ProfileVIewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.ui.graphics.Color
import com.izamaralv.swipethebeat.common.backgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun STBTopAppBar(profileViewModel: ProfileVIewModel, onLogout: () -> Unit) {
    val profileImageUrl = profileViewModel.profileImageUrl.observeAsState()
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu Icon")
            }
        },
        title = {
            Image(
                painter = painterResource(id = R.drawable.plain_logo),
                contentDescription = "App logo",
                modifier = Modifier.size(135.dp)
            )
        },
        actions = {
            profileImageUrl.value?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(model = url),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, backgroundColor, CircleShape)
                        ,
                    contentScale = ContentScale.Crop
                )
            }
            IconButton(onClick = onLogout) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "null")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = softComponentColor,
        )


    )

}
