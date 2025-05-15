package com.izamaralv.swipethebeat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.izamaralv.swipethebeat.navigation.NavGraph
import com.izamaralv.swipethebeat.repository.UserRepository
import com.izamaralv.swipethebeat.ui.components.NotificationHelper
import com.izamaralv.swipethebeat.ui.theme.SwipeTheBeatTheme
import com.izamaralv.swipethebeat.utils.Credentials
import com.izamaralv.swipethebeat.utils.ProfileManager
import com.izamaralv.swipethebeat.utils.SpotifyApi
import com.izamaralv.swipethebeat.utils.SpotifyManager
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.InitializationViewModel
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100 // Código de solicitud, puede ser cualquier número
                )
            }
        }

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
                            profileViewModel = profileViewModel,
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
            // ✅ Initialize Spotify components
            spotifyManager.initializeSpotifyClient(accessToken, refreshToken, expiresIn)
            spotifyManager.initializeSongRepository(accessToken)
            profileManager.fetchUserProfile(accessToken)

            // ✅ Use coroutine for background processing
            CoroutineScope(Dispatchers.IO).launch {
                val spotifyUserProfile = SpotifyApi.getUserProfile(accessToken, applicationContext) // ✅ Fetch latest Spotify data
                val spotifyUserId = spotifyUserProfile?.get("user_id")

                withContext(Dispatchers.Main) { // ✅ Ensure UI updates happen on the main thread
                    if (spotifyUserId.isNullOrEmpty()) {
                        Log.e("Firestore", "❌ Spotify User ID is null—Firestore can't retrieve profile!")
                    } else {
                        Log.d("Firestore", "✅ Using Spotify User ID: $spotifyUserId for Firestore")

                        // ✅ Ensure data is properly formatted for Firestore
                        val userProfileMap = spotifyUserProfile.mapValues { it.value ?: "" }
                        Log.d("Firestore", "✅ Updating Firestore with latest Spotify profile data: $userProfileMap")

                        // ✅ Call Firestore update function in UserRepository
                        val userRepository = UserRepository()
                        userRepository.saveUserToFirestore(userProfileMap)

                        profileViewModel.loadUserProfile(spotifyUserId)
                    }

                    initializationViewModel.setInitialized()
                }
            }
        } else {
            // ✅ Start the OAuth authentication flow if no tokens exist
            initiateOAuthFlow()
        }
    }

    private fun initiateOAuthFlow() {
        val clientId = Credentials.CLIENT_ID
        val redirectUri = Credentials.REDIRECT_URI
        val authorizationUrl = spotifyManager.getAuthorizationUrl(clientId, redirectUri)
        val intent = Intent(Intent.ACTION_VIEW, authorizationUrl.toUri())
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
                        Log.d("MainActivity", "Access Token: $accessToken") // Registra el token también aquí
                        spotifyManager.initializeSpotifyClient(accessToken, refreshToken, 3600)
                        spotifyManager.initializeSongRepository(accessToken)
                        profileManager.fetchUserProfile(accessToken)

                        runOnUiThread {
                            initializationViewModel.setInitialized()
                            navController.navigate("main_screen") {
                                popUpTo("login_screen") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkTokenAndNavigate() {
        val tokenManager = TokenManager(applicationContext)
        val accessToken = tokenManager.getAccessToken()
        if (accessToken != null) {
            navController.navigate("main_screen") {
                popUpTo("login_screen") { inclusive = true }
            }
        }
    }

    private fun logout() {
        spotifyManager.logout(navController)
    }
}
