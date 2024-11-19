package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun startSpotifyLogin(context: Context, clientId: String, redirectUri: String) {
    val authUrl = "https://accounts.spotify.com/authorize?client_id=$clientId&response_type=code&redirect_uri=$redirectUri&scope=user-read-private%20user-read-email"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
    context.startActivity(intent)
}
