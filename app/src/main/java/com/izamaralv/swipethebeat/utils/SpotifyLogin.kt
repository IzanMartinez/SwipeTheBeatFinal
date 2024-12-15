package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.izamaralv.swipethebeat.utils.Credentials.CLIENT_ID
import com.izamaralv.swipethebeat.utils.Credentials.REDIRECT_URI

fun startSpotifyLogin(context: Context) {
    val spotifyManager = SpotifyManager(context)
    var authUrl = spotifyManager.getAuthorizationUrl(CLIENT_ID, REDIRECT_URI)
    // Ensure the login dialog always pops up
    authUrl += "&show_dialog=true"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
    context.startActivity(intent)
}
