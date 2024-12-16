package com.izamaralv.swipethebeat.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.ui.theme.bluePastelColor
import com.izamaralv.swipethebeat.ui.theme.greenPastelColor
import com.izamaralv.swipethebeat.ui.theme.orangePastelColor
import com.izamaralv.swipethebeat.ui.theme.purplePastelColor
import com.izamaralv.swipethebeat.ui.theme.redPastelColor
import com.izamaralv.swipethebeat.ui.theme.pinkPastelColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun STBTopAppBar(profileViewModel: ProfileViewModel, onLogout: () -> Unit) {
    val profileImageUrl by profileViewModel.profileImageUrl.observeAsState()
    var iconMenuExpanded by remember { mutableStateOf(false) }
    var colorMenuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // Log the profile image URL
    Log.d("STBTopAppBar", "Profile image URL: $profileImageUrl")

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(
                onClick = { colorMenuExpanded = !colorMenuExpanded }) {
                Icon(imageVector = Icons.Default.Palette, contentDescription = "palette icon")
            }



            DropdownMenu(
                expanded = colorMenuExpanded,
                onDismissRequest = { colorMenuExpanded = false },
                modifier = Modifier
                    .background(color = cardColor.value)
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.green),
                            contentDescription = "Green color",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    },
                    text = { Text("Green", color = greenPastelColor) },
                    onClick = {
                        colorMenuExpanded = false
                        softComponentColor.value = greenPastelColor
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                DropdownMenuItem(
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.orange),
                            contentDescription = "Orange color",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    },
                    text = { Text("Orange", color = orangePastelColor) },
                    onClick = {
                        colorMenuExpanded = false
                        softComponentColor.value = orangePastelColor
                    },
                    modifier = Modifier.padding(bottom = 10.dp)

                )

                DropdownMenuItem(
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.blue),
                            contentDescription = "Blue color",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    },
                    text = { Text("Blue", color = bluePastelColor) },
                    onClick = {
                        colorMenuExpanded = false
                        softComponentColor.value = bluePastelColor
                    },
                    modifier = Modifier.padding(bottom = 10.dp)

                )

                DropdownMenuItem(
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.red),
                            contentDescription = "Red color",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    },
                    text = { Text("Red", color = redPastelColor) },
                    onClick = {
                        colorMenuExpanded = false
                        softComponentColor.value = redPastelColor
                    },
                    modifier = Modifier.padding(bottom = 10.dp)

                )

                DropdownMenuItem(
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.purple),
                            contentDescription = "Purple color",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    },
                    text = { Text("Purple", color = purplePastelColor) },
                    onClick = {
                        colorMenuExpanded = false
                        softComponentColor.value = purplePastelColor
                    },
                    modifier = Modifier.padding(bottom = 10.dp)

                )

                DropdownMenuItem(
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.pink),
                            contentDescription = "Pink color",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    },
                    text = { Text("Pink", color = pinkPastelColor) },
                    onClick = {
                        colorMenuExpanded = false
                        softComponentColor.value = pinkPastelColor
                    },
                    modifier = Modifier.padding(bottom = 10.dp)

                )


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
            profileImageUrl?.let { url ->
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build()
                )
                Log.d("STBTopAppBar", "Loading profile image from URL: $url")
                Box(
                    modifier = Modifier
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
                        modifier = Modifier
                            .background(color = softComponentColor.value)
                    )
                    {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.Help,
                                    contentDescription = "Help icon"
                                )
                            },
                            text = { Text("¿Algún problema?") },
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:swipethebeathelp@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Need assistance")
                                }
                                context.startActivity(intent)
                                iconMenuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Logout icon"
                                )
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

