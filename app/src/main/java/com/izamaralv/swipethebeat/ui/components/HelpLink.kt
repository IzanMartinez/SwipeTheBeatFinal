package com.izamaralv.swipethebeat.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.izamaralv.swipethebeat.ui.theme.greenPastelColor

@Composable
fun HelpLink(text: String, context: android.content.Context = LocalContext.current, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = greenPastelColor,
        textDecoration = TextDecoration.Underline,
        lineHeight = 8.75.em,
        style = TextStyle(
            fontSize = 16.sp
        ),
        modifier = Modifier.clickable {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:swipethebeathelp@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Need assistance")
            }
            context.startActivity(intent)
        }
    )
}
