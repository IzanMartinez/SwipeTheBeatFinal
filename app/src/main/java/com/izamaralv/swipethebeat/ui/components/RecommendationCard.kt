package com.izamaralv.swipethebeat.ui.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.models.Track

@Composable
fun RecommendationCard(
    track: Track,
    onSave: () -> Unit,
    onLike: () -> Unit,
    onDislike: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.red_arrow),
                contentDescription = "Skip",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onDislike() }
            )
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = softComponentColor.value)
            ) {
                Text(text = "Guardar", color = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Save",
                    tint = Color.Black
                )
            }
            Image(
                painter = painterResource(id = R.drawable.green_arrow),
                contentDescription = "Like",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onLike() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TinderCard(
            onSwipeLeft = onDislike,
            onSwipeRight = onLike
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(track.album.images.first().url),
                        contentDescription = track.name,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = track.name,
                        style = TextStyle(
                            color = softComponentColor.value,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = track.artists.joinToString(", ") { it.name },
                        style = TextStyle(
                            color = softComponentColor.value,
                            fontSize = 18.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = track.album.name,
                        style = TextStyle(
                            color = softComponentColor.value,
                            fontSize = 16.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        IconButton(
            onClick = {
                val spotifyUri = "spotify:track:${track.id}"
                val intent = Intent(Intent.ACTION_VIEW, spotifyUri.toUri())
                intent.putExtra(
                    Intent.EXTRA_REFERRER,
                    "android-app://${context.packageName}".toUri()
                )
                context.startActivity(intent)
            },
            modifier = Modifier
                .size(60.dp)
                .background(softComponentColor.value, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
