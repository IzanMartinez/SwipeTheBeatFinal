package com.izamaralv.swipethebeat.utils

import android.content.Context
import android.util.Log
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.getSpotifyAuthorizationUrl
import com.izamaralv.swipethebeat.repository.SongRepository
import com.izamaralv.swipethebeat.viewmodel.SongViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpotifyManager(private val context: Context) {

    private lateinit var songViewModel: SongViewModel

    fun initializeSpotifyClient(accessToken: String, refreshToken: String, expiresIn: Int) {
        // Inicializa SpotifyClient con los tokens y el tiempo de expiración
        Log.d("SpotifyManager", "Initializing SpotifyClient with accessToken = $accessToken, refreshToken = $refreshToken, expiresIn = $expiresIn")
        SpotifyClient.initialize(accessToken, refreshToken, expiresIn)
        // Fuerza la actualización del token para verificar su manejo
        SpotifyClient.forceTokenRefresh()
        Log.d("SpotifyManager", "SpotifyClient initialized")
    }

    fun initializeSongRepository(accessToken: String) {
        // Inicializa SongRepository con el token de acceso
        Log.d("SpotifyManager", "Initializing SongRepository with accessToken = $accessToken")
        val songRepository = SongRepository()
        val songViewModelFactory = SongViewModelFactory(songRepository, accessToken)
        songViewModel = songViewModelFactory.create(SongViewModel::class.java)
        Log.d("SpotifyManager", "SongRepository and SongViewModel initialized")

        // Carga las recomendaciones iniciales
        songViewModel.loadInitialRecommendationsInternal()

    }

    fun exchangeCodeForToken(code: String, onTokenReceived: (accessToken: String, refreshToken: String) -> Unit) {
        // Intercambia el código por tokens de acceso y actualización
        CoroutineScope(Dispatchers.IO).launch {
            val tokens = SpotifyApi.exchangeCodeForToken(code)
            tokens?.let { (accessToken, refreshToken) ->
                val tokenManager = TokenManager(context)
                tokenManager.saveTokens(accessToken, refreshToken)
                onTokenReceived(accessToken, refreshToken)
            } ?: run {
                Log.e("SpotifyManager", "Token exchange failed, tokens are null")
            }
        }
    }

    fun getAuthorizationUrl(clientId: String, redirectUri: String): String {
        // Define los alcances necesarios para la autorización
        val scopes = listOf(
            SpotifyScope.USER_READ_PRIVATE,          // Acceso a la información del perfil del usuario (nombre, imagen, etc.)
            SpotifyScope.USER_READ_EMAIL,            // Acceso al correo electrónico del usuario
            SpotifyScope.USER_LIBRARY_READ,          // Recuperar canciones guardadas
            SpotifyScope.USER_LIBRARY_MODIFY,        // Guardar canciones
            SpotifyScope.USER_READ_RECENTLY_PLAYED,  // Acceso a canciones reproducidas recientemente
            SpotifyScope.PLAYLIST_READ_PRIVATE,      // Necesario para acceder a playlists
            SpotifyScope.USER_TOP_READ               // Recuperar las mejores canciones/artistas
        )

        // Obtiene la URL de autorización de Spotify con los alcances definidos
        val authorizationUrl = getSpotifyAuthorizationUrl(
            *scopes.toTypedArray(),
            clientId = clientId,
            redirectUri = redirectUri
        )

        return authorizationUrl
    }


}

