package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

object SpotifyConnection {

    private var spotifyAppRemote: SpotifyAppRemote? = null

    fun connectToSpotify(
        context: Context,
        clientId: String,
        redirectUri: String,
        onConnected: (SpotifyAppRemote) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        SpotifyAppRemote.connect(
            context,
            ConnectionParams.Builder(clientId)
                .setRedirectUri(redirectUri)
                .showAuthView(true)
                .build(),
            object : Connector.ConnectionListener {
                override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    Log.d("SpotifyConnectionUtil", "Successfully connected to Spotify.")
                    onConnected(appRemote)
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("SpotifyConnectionUtil", "Failed to connect: ${throwable.message}", throwable)
                    onError(throwable)
                }
            }
        )

    }

    fun disconnectSpotify() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            spotifyAppRemote = null
            Log.d("SpotifyConnectionUtil", "Disconnected from Spotify.")
        }
    }
}
