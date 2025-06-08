package com.izamaralv.swipethebeat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.izamaralv.swipethebeat.navigation.NavGraph
import com.izamaralv.swipethebeat.navigation.Screen
import com.izamaralv.swipethebeat.repository.SongRepository
import com.izamaralv.swipethebeat.repository.UserRepository
import com.izamaralv.swipethebeat.ui.components.NotificationHelper
import com.izamaralv.swipethebeat.utils.Credentials
import com.izamaralv.swipethebeat.utils.ProfileManager
import com.izamaralv.swipethebeat.utils.SpotifyApi
import com.izamaralv.swipethebeat.utils.SpotifyManager
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.GeminiRecommendationViewModel
import com.izamaralv.swipethebeat.viewmodel.GeminiRecommendationViewModelFactory
import com.izamaralv.swipethebeat.viewmodel.InitializationViewModel
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel
import com.izamaralv.swipethebeat.viewmodel.SearchViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModel
import com.izamaralv.swipethebeat.viewmodel.SongViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private lateinit var spotifyManager: SpotifyManager
    private lateinit var profileManager: ProfileManager
    private lateinit var songRepository: SongRepository

    private val profileViewModel: ProfileViewModel by viewModels()
    private val initializationViewModel: InitializationViewModel by viewModels()
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var songViewModel: SongViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        songRepository = SongRepository()
        val accessToken = TokenManager(applicationContext).getAccessToken() ?: ""

        songViewModel = SongViewModelFactory(songRepository, accessToken)
            .create(SongViewModel::class.java)

        searchViewModel = SearchViewModel(songRepository)
        Log.d("MainActivity", "🚀 SearchViewModel initialized: $searchViewModel")

        // Request notification permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }

        NotificationHelper.createNotificationChannel(this)
        spotifyManager = SpotifyManager(applicationContext)
        profileManager = ProfileManager(applicationContext, profileViewModel)

        fetchUserProfileOnStart()

        initializationViewModel.isInitialized.observe(this) { isInitialized ->
            if (isInitialized) {
                setContent {
                    navController = rememberNavController()

                    val tokenManager = TokenManager(applicationContext)
                    val geminiVm: GeminiRecommendationViewModel = viewModels<GeminiRecommendationViewModel> {
                        GeminiRecommendationViewModelFactory(
                            songRepository   = songRepository,
                            profileViewModel = profileViewModel,
                            geminiClient     = com.izamaralv.swipethebeat.utils.GeminiClient,
                            tokenManager     = tokenManager,
                            userRepository   = UserRepository()
                        )
                    }.value

                    // 2) Lanzamos la carga asíncrona en cuanto la Composable entre en composición
                    LaunchedEffect(Unit) {
                        Log.d("MainActivity", "Triggering Gemini loadRecommendations() test")
                        geminiVm.loadRecommendations()
                    }

                    NavGraph(
                        navController = navController,
                        profileViewModel = profileViewModel,
                        searchViewModel = searchViewModel,
                        songViewModel = songViewModel,
                        geminiViewModel = geminiVm
                    )

                    checkTokenAndNavigate()
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
            spotifyManager.initializeSongRepository(accessToken)
            profileManager.fetchUserProfile(accessToken)

            CoroutineScope(Dispatchers.IO).launch {
                val spotifyUserProfile = SpotifyApi.getUserProfile(accessToken, applicationContext)
                val spotifyUserId = spotifyUserProfile?.get("user_id")

                withContext(Dispatchers.Main) {
                    if (spotifyUserId.isNullOrEmpty()) {
                        Log.e("Firestore", "❌ Spotify User ID is null—Firestore can't retrieve profile!")
                    } else {
                        val userRepository = UserRepository()
                        val userProfileMap = spotifyUserProfile.mapValues { it.value ?: "" }
                        userRepository.saveUserToFirestore(userProfileMap)
                        profileViewModel.loadUserProfile(spotifyUserId)

                        initializationViewModel.setInitialized()
                    }
                }
            }
        } else {
            initiateOAuthFlow()
        }
    }

    private fun initiateOAuthFlow() {
        val clientId = Credentials.SPOTIFY_CLIENT_ID
        val redirectUri = Credentials.REDIRECT_URI
        val authorizationUrl = spotifyManager.getAuthorizationUrl(clientId, redirectUri)
        startActivity(Intent(Intent.ACTION_VIEW, authorizationUrl.toUri()))
    }

    override fun onResume() {
        super.onResume()
        intent?.data?.let { uri ->
            if (uri.scheme == "myapp" && uri.host == "callback") {
                val code = uri.getQueryParameter("code")
                code?.let {
                    spotifyManager.exchangeCodeForToken(it) { accessToken, refreshToken ->
                        spotifyManager.initializeSpotifyClient(accessToken, refreshToken, 3600)
                        spotifyManager.initializeSongRepository(accessToken)
                        profileManager.fetchUserProfile(accessToken)

                        runOnUiThread {
                            initializationViewModel.setInitialized()
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkTokenAndNavigate() {
        val accessToken = TokenManager(applicationContext).getAccessToken()
        if (accessToken != null) {
            navController.navigate(Screen.Lobby.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }
}
