package com.izamaralv.swipethebeat.screens

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.basicBorder
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.common.textColor
import com.izamaralv.swipethebeat.ui.components.ArtistSearchBar
import com.izamaralv.swipethebeat.ui.components.ColorPickerMenu
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import com.izamaralv.swipethebeat.viewmodel.SearchViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    searchViewModel: SearchViewModel
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = backgroundColor.value, darkIcons = false)
    val context = LocalContext.current

    val profileImageUrl = profileViewModel.getProfileImageUrl()
    val displayName = profileViewModel.getDisplayName()

    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken()

    val painter = if (profileImageUrl.isEmpty()) {
        painterResource(id = R.drawable.default_profile)
    } else {
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(context).data(profileImageUrl).crossfade(true).build()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.value)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 40.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painter,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .border(5.dp, softComponentColor.value, CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
                .height(500.dp)
                .background(color = cardColor.value, shape = RoundedCornerShape(16.dp))
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.height(100.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Nombre de usuario:",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor.value
                    )
                )
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = profileViewModel.getDisplayName(),
                    style = TextStyle(fontSize = 24.sp, color = softComponentColor.value)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.height(120.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Artista favorito:",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor.value
                    )
                )

                if (accessToken != null) {
                    ArtistSearchBar(searchViewModel, accessToken) { selectedArtist ->
                        Log.d("ProfileScreen", "🔥 User selected artist: $selectedArtist")
                    }
                } else {
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        text = "⚠ No access token found!",
                        style = TextStyle(fontSize = 20.sp, color = Color.Red)
                    )
                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            ColorPickerMenu(profileViewModel)
        }
    }
}
