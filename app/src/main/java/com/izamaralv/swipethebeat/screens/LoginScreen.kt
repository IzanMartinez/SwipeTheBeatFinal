package com.izamaralv.swipethebeat.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.ui.theme.StandardComponent
import com.izamaralv.swipethebeat.ui.theme.StandardField
import com.izamaralv.swipethebeat.utils.startSpotifyLogin

@Composable
fun LoginScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // Overall Screen Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(top = 60.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .requiredWidth(412.dp)
                    .requiredHeight(386.dp)
            )
            // Title
            Text(
                text = "Swipe The Beat",
                color = StandardField,
                textAlign = TextAlign.Center,
                lineHeight = 3.5.em,
                style = TextStyle(
                    fontSize = 40.sp
                ),
                modifier = Modifier
                    .padding(top = 8.dp) // Padding between logo and title
                    .border(border = BorderStroke(1.dp, Color.Black))
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp)) // Space between title and button
        }

        // Spotify Login Button
        Column(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(40.dp))
                    .background(color = StandardComponent)
                    .padding(horizontal = 25.dp, vertical = 10.dp)
                    .clickable {
                        startSpotifyLogin(
                            context = context,
                            clientId = "9ce30545b1c64f29844917fae59145c7",
                            redirectUri = "myapp://callback"
                        )
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.spotify_logo),
                    contentDescription = "Spotify Logo",
                    modifier = Modifier
                        .requiredWidth(41.dp)
                        .requiredHeight(38.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Inicia sesión con Spotify",
                    color = StandardField,
                    lineHeight = 8.75.em,
                    style = TextStyle(fontSize = 16.sp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp)) // Space between button and text link

            // TextLink Composable
            TextLink()
        }
    }
}

@Composable
fun TextLink(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = "¿Necesitas ayuda? Contacta con soporte",
            color = Color(0xff60ff35),
            textDecoration = TextDecoration.Underline,
            lineHeight = 8.75.em,
            style = TextStyle(
                fontSize = 16.sp
            )
        )
        Spacer(modifier = Modifier.height(40.dp))
    }
}
