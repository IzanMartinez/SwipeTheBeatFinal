package com.izamaralv.swipethebeat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.izamaralv.swipethebeat.navigation.NavGraph
import com.izamaralv.swipethebeat.repository.SongRepository
import com.izamaralv.swipethebeat.ui.theme.SwipeTheBeatTheme
import com.izamaralv.swipethebeat.utils.SpotifyApi
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.ProfileVIewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var songViewModel: SongViewModel
    private val profileViewModel = ProfileVIewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize songRepository and songViewModel
        val songRepository = SongRepository(applicationContext)
        songViewModel = SongViewModel(songRepository)

        // Make the status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            SwipeTheBeatTheme {
                navController = rememberNavController()
                NavGraph(navController = navController, profileViewModel, songViewModel)
                checkTokenAndNavigate()
            }
        }

        // Hide the status bar
//        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
//        actionBar?.hide()

        // Check for token and fetch profile on app start
        fetchUserProfileOnStart()
    }

    private fun fetchUserProfileOnStart() {
        val tokenManager = TokenManager(applicationContext)
        val accessToken = tokenManager.getAccessToken()
        Log.d("MainActivity", "Retrieved access token: $accessToken")
        if (accessToken != null) {
            Log.d("MainActivity", "Access token found, fetching user profile")
            fetchUserProfile(accessToken)
        } else {
            Log.d("MainActivity", "No access token found, staying on login_screen")
        }
    }


    private fun checkTokenAndNavigate() {
        Log.d("MainActivity", "checkTokenAndNavigate called")
        val tokenManager = TokenManager(applicationContext)
        val accessToken = tokenManager.getAccessToken()
        Log.d("MainActivity", "Retrieved access token: $accessToken")
        if (accessToken != null) {
            Log.d("MainActivity", "Access token found, navigating to main_screen")
            navController.navigate("main_screen") {
                popUpTo("login_screen") { inclusive = true }
            }
        } else {
            Log.d("MainActivity", "No access token found, staying on login_screen")
        }
    }


    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
        val intent = intent
        intent?.data?.let { uri ->
            Log.d("MainActivity", "Received URI: $uri")
            if (uri.scheme == "myapp" && uri.host == "callback") {
                val code = uri.getQueryParameter("code")
                Log.d("MainActivity", "Received code: $code")
                code?.let {
                    exchangeCodeForToken(it)
                }
            }
        }
    }

    private fun exchangeCodeForToken(code: String) {
        Log.d("UserProfile", "exchangeCodeForToken called with code: $code")
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("UserProfile", "Launching coroutine to exchange code for token")
            val tokens = SpotifyApi.exchangeCodeForToken(code)
            tokens?.let { (accessToken, refreshToken) ->
                val tokenManager = TokenManager(applicationContext)
                tokenManager.saveTokens(accessToken, refreshToken)
                Log.d("UserProfile", "Tokens saved: Access token - $accessToken, Refresh token - $refreshToken")

                // Fetch user profile
                fetchUserProfile(accessToken)

                withContext(Dispatchers.Main) {
                    Log.d("UserProfile", "Navigating to main_screen")
                    navController.navigate("main_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
            }
        }
    }


    private fun fetchUserProfile(accessToken: String) {
        Log.d("UserProfile", "Fetching user profile")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userProfile = SpotifyApi.getUserProfile(accessToken)
                Log.d("UserProfile", "User profile fetched: $userProfile")

                userProfile?.let { profile ->
                    // Log the entire JSON response to understand its structure
                    Log.d("UserProfile", "User profile JSON: ${profile.toString(2)}")

                    val displayName = profile.optString("display_name", "No Name")
                    val profileImageUrl = profile.getJSONArray("images").optJSONObject(0)?.getString("url") ?: ""

                    Log.d("UserProfile", "User display name: $displayName")
                    Log.d("UserProfile", "User profile image URL: $profileImageUrl")

                    // Update ViewModel
                    profileViewModel.displayName.postValue(displayName)
                    profileViewModel.profileImageUrl.postValue(profileImageUrl)
                    Log.d("UserProfile", "ProfileViewModel updated")
                }
            } catch (e: IOException) {
                if (e.message?.contains("401 Unauthorized") == true) {
                    Log.d("UserProfile", "Token expired, refreshing token")
                    val tokenManager = TokenManager(applicationContext)
                    val refreshToken = tokenManager.getRefreshToken() // Assuming you save refresh token

                    refreshToken?.let {
                        Log.d("UserProfile", "Attempting to refresh token with refresh token: $it")
                        val newAccessToken = SpotifyApi.refreshAccessToken(it)
                        Log.d("UserProfile", "New access token: $newAccessToken")
                        newAccessToken?.let { newToken ->
                            tokenManager.saveTokens(newToken, it)
                            fetchUserProfile(newToken) // Retry fetching profile with new token
                        }
                    }
                } else {
                    Log.e("UserProfile", "Failed to fetch user profile: ${e.message}")
                }
            }
        }
    }
    private fun logout() {
        val tokenManager = TokenManager(applicationContext)
        tokenManager.clearTokens()
        Log.d("MainActivity", "Tokens cleared, navigating to login_screen")
        navController.navigate("login_screen") {
            popUpTo("main_screen") { inclusive = true }
        }
    }


}