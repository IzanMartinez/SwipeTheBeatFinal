package com.izamaralv.swipethebeat.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.textColor
import com.izamaralv.swipethebeat.ui.theme.StandardComponent
import com.izamaralv.swipethebeat.ui.theme.StandardField
import com.izamaralv.swipethebeat.utils.startSpotifyLogin

@Composable
fun OldLoginScreen() {
    val context = LocalContext.current
    // Overall Screen Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // Title Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp),  // Increased padding to move the logo and text further up
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .requiredWidth(412.dp)
                    .requiredHeight(330.dp)
            )
            Text(
                text = "Swipe The Beat",
                color = StandardField,
                textAlign = TextAlign.Center,
                lineHeight = 3.5.em,
                style = TextStyle(
                    fontSize = 40.sp
                ),
                modifier = Modifier
                    .padding(top = 1.dp) // Adjusted padding to reduce space between image and text
                    .border(border = BorderStroke(1.dp, Color.Black))
                    .padding(1.dp)
            )
        }

        // Spotify Login Button
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(40.dp))
                .background(color = StandardComponent)
                .padding(horizontal = 25.dp, vertical = 10.dp)
                .clickable { startSpotifyLogin(context) }
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

        Spacer(modifier = Modifier.padding(80.dp))

        // Login and Sign-up Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            ClickableText(
                text = AnnotatedString("¿Necesitas ayuda? Contacta con soporte"),
                onClick = { /* TODO: add navController*/ },
                style = TextStyle(
                    textDecoration = TextDecoration.Underline, color = textColor.value
                )
            )
        }
    }
}
