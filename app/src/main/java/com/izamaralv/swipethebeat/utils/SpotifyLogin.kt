package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.izamaralv.swipethebeat.utils.Credentials.CLIENT_ID
import com.izamaralv.swipethebeat.utils.Credentials.REDIRECT_URI
import androidx.core.net.toUri

fun startSpotifyLogin(context: Context) {
    val spotifyManager = SpotifyManager(context)
    var authUrl = spotifyManager.getAuthorizationUrl(CLIENT_ID, REDIRECT_URI)

    // Asegura que el cuadro de diálogo de inicio de sesión siempre aparezca
    authUrl += "&show_dialog=true"

    // Crea un intent para abrir la URL de autorización de Spotify
    val intent = Intent(Intent.ACTION_VIEW, authUrl.toUri())
    context.startActivity(intent) // Inicia la actividad para la autorización
}
