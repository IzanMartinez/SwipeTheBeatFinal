package com.izamaralv.swipethebeat.ui.components

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.softComponentColor
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun STBTopAppBar(
    profileViewModel: ProfileViewModel,
    onLogout: () -> Unit,
    firstOption: String,
    firstFunction: () -> Unit,
    firstIcon: ImageVector
) {
    // Observa la URL de la imagen del perfil
    val profileImageUrl = profileViewModel.getProfileImageUrl()
    var iconMenuExpanded by remember { mutableStateOf(false) }
    var colorMenuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current


    // Registra la URL de la imagen del perfil
    Log.d("STBTopAppBar", "Profile image URL: $profileImageUrl")

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = { colorMenuExpanded = !colorMenuExpanded }) {
                Icon(imageVector = Icons.Default.Palette, contentDescription = "palette icon")
            }

            if (colorMenuExpanded) {
                ColorPickerMenu(profileViewModel = profileViewModel, onDismiss = { colorMenuExpanded = false })
            }
        },
        title = {
            Image(
                painter = painterResource(id = R.drawable.logo_no_background),
                contentDescription = "App logo",
                modifier = Modifier.size(135.dp)
            )
        },
        actions = {

            profileImageUrl.let { url ->
        //                val painter = painterResource(id = R.drawable.default_profile)

                val painter = if (profileImageUrl.isEmpty()){
                    painterResource(id = R.drawable.default_profile)
                } else {
                    rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)
                            .build()
                    )
                }

                rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build()
                )
                Log.d("STBTopAppBar", "Loading profile image from URL: $url")
                Box(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, backgroundColor.value, CircleShape)
                        .clickable { iconMenuExpanded = !iconMenuExpanded }
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, backgroundColor.value, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    DropdownMenu(
                        expanded = iconMenuExpanded,
                        onDismissRequest = { iconMenuExpanded = false },
                        modifier = Modifier.background(color = softComponentColor.value)
                    ) {
                        // Opción principal
                        DropdownMenuItem(
                            leadingIcon = { Icon(firstIcon, "") },
                            text = { Text(firstOption) },
                            onClick = {
                                firstFunction()
                                iconMenuExpanded = false
                            }
                        )
                        // Opción de ayuda
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "Help icon")
                            },
                            text = { Text("¿Algún problema?") },
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:swipethebeathelp@gmail.com".toUri()
                                    putExtra(Intent.EXTRA_SUBJECT, "Need assistance")
                                }
                                context.startActivity(intent)
                                iconMenuExpanded = false
                            }
                        )
                        // Opción de cerrar sesión
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout icon")
                            },
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                onLogout()
                                iconMenuExpanded = false
                            }
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = softComponentColor.value,
        )
    )
}

