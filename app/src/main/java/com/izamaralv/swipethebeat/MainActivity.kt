package com.izamaralv.swipethebeat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.izamaralv.swipethebeat.navigation.NavGraph
import com.izamaralv.swipethebeat.ui.theme.SwipeTheBeatTheme
import com.izamaralv.swipethebeat.utils.Credentials
import com.izamaralv.swipethebeat.utils.ProfileManager
import com.izamaralv.swipethebeat.utils.SpotifyManager
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.InitializationViewModel
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var spotifyManager: SpotifyManager
    private lateinit var profileManager: ProfileManager
    private val profileViewModel: ProfileViewModel by viewModels()
    private val initializationViewModel: InitializationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make the status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // Initialize managers
        spotifyManager = SpotifyManager(applicationContext)
        profileManager = ProfileManager(applicationContext, profileViewModel)

        fetchUserProfileOnStart()

        // Observe initialization status and set content
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
            } else {
                // Show a loading indicator or splash screen
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
            spotifyManager.initializeSongRepository(accessToken)
            profileManager.fetchUserProfile(accessToken)
            initializationViewModel.setInitialized()
        } else {
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
                        Log.d("MainActivity", "Access Token: $accessToken") // Log the token here as well
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
