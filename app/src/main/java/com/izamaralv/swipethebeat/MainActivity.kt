package com.izamaralv.swipethebeat

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.izamaralv.swipethebeat.navigation.NavGraph
import com.izamaralv.swipethebeat.ui.components.NotificationHelper
import com.izamaralv.swipethebeat.ui.theme.SwipeTheBeatTheme
import com.izamaralv.swipethebeat.utils.Credentials
import com.izamaralv.swipethebeat.utils.SpotifyManager
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.InitializationViewModel

class MainActivity : ComponentActivity() {
    // Declaración de variables para la navegación y gestión de Spotify y perfiles
    private lateinit var navController: NavHostController
    private lateinit var spotifyManager: SpotifyManager
    private lateinit var profileManager: ProfileManager
    private val profileViewModel: ProfileViewModel by viewModels()
    private val initializationViewModel: InitializationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicita permisos para notificaciones en versiones superiores a TIRAMISU

        // Crea el canal de notificaciones
        NotificationHelper.createNotificationChannel(this)

        // Inicializa los gestores
        spotifyManager = SpotifyManager(applicationContext)
        profileManager = ProfileManager(applicationContext, profileViewModel)
        // Obtiene el perfil del usuario al iniciar
        fetchUserProfileOnStart()

        // Observa el estado de inicialización y establece el contenido
        initializationViewModel.isInitialized.observe(this) { isInitialized ->
            if (isInitialized) {
                setContent {
                    SwipeTheBeatTheme {
                        navController = rememberNavController()
                        NavGraph(
                            navController = navController,
                            profileViewModel = profileViewModel
                        )
                        checkTokenAndNavigate()
                    }
                }
            }
        }
    }

    private fun fetchUserProfileOnStart() {
        val tokenManager = TokenManager(applicationContext)
        val accessToken = tokenManager.getAccessToken()
        val refreshToken = tokenManager.getRefreshToken()
        val expiresIn = 3600

        if (accessToken != null && refreshToken != null) {
            spotifyManager.initializeSpotifyClient(accessToken, refreshToken, expiresIn)
            profileManager.fetchUserProfile(accessToken)
            initializationViewModel.setInitialized()
        } else {
            // Inicia el flujo de autenticación OAuth si no hay tokens
            initiateOAuthFlow()
        }
    }

    private fun initiateOAuthFlow() {
        val clientId = Credentials.CLIENT_ID
        val redirectUri = Credentials.REDIRECT_URI
        val authorizationUrl = spotifyManager.getAuthorizationUrl(clientId, redirectUri)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        intent?.data?.let { uri ->
            if (uri.scheme == "myapp" && uri.host == "callback") {
                val code = uri.getQueryParameter("code")
                code?.let {
                    spotifyManager.exchangeCodeForToken(it) { accessToken, refreshToken ->
                        Log.d("MainActivity", "Access Token: $accessToken")
                        spotifyManager.initializeSpotifyClient(accessToken, refreshToken, 3600)
                        profileManager.fetchUserProfile(accessToken)

                        runOnUiThread {
                            initializationViewModel.setInitialized()
                            navController.navigate("main_screen") {
                                popUpTo("login_screen") { inclusive = true }
                            }
        }
    }

    private fun logout() {
        spotifyManager.logout(navController)
    }
}

