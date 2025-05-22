package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.izamaralv.swipethebeat.utils.Credentials.REDIRECT_URI
import com.izamaralv.swipethebeat.utils.Credentials.SPOTIFY_CLIENT_ID

fun startSpotifyLogin(context: Context) {
    val spotifyManager = SpotifyManager(context)
    var authUrl = spotifyManager.getAuthorizationUrl(SPOTIFY_CLIENT_ID, REDIRECT_URI)

    // Asegura que el cuadro de diálogo de inicio de sesión siempre aparezca
    authUrl += "&show_dialog=true"

    // Crea un intent para abrir la URL de autorización de Spotify
    val intent = Intent(Intent.ACTION_VIEW, authUrl.toUri())
    context.startActivity(intent) // Inicia la actividad para la autorización
}
