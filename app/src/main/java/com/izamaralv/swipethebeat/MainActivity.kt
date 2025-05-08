package com.izamaralv.swipethebeat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.izamaralv.swipethebeat.navigation.NavGraph
import com.izamaralv.swipethebeat.ui.components.NotificationHelper
import com.izamaralv.swipethebeat.ui.theme.SwipeTheBeatTheme
import com.izamaralv.swipethebeat.utils.ProfileManagerFirebase
import com.izamaralv.swipethebeat.viewmodel.InitializationViewModel
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {
    // Declaración de variables para la navegación y gestión de Spotify y perfiles
    private lateinit var navController:NavHostController
    private lateinit var profileManager: ProfileManagerFirebase
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
                        checkUserAndNavigate()
                    }
                }
            }
        }
    }

    private fun checkUserAndNavigate() {
        lifecycleScope.launchWhenCreated {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // User is logged in, go to main screen
                navController.navigate("main_screen") {
                    popUpTo("login_screen") { inclusive = true }
                }
            } else {
                // No user logged in, go to login screen
                navController.navigate("login_screen") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
                initializationViewModel.setInitialized()
            }
        }
    }
}
