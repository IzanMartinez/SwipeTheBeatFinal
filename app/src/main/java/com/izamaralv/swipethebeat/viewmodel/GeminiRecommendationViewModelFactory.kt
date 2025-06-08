package com.izamaralv.swipethebeat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.izamaralv.swipethebeat.repository.SongRepository
import com.izamaralv.swipethebeat.repository.UserRepository
import com.izamaralv.swipethebeat.utils.GeminiClient
import com.izamaralv.swipethebeat.utils.TokenManager

/**
 * Factory para crear instancias de GeminiRecommendationViewModel con las dependencias necesarias.
 */
class GeminiRecommendationViewModelFactory(
    private val songRepository: SongRepository,
    private val profileViewModel: ProfileViewModel,
    private val geminiClient: GeminiClient,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GeminiRecommendationViewModel::class.java)) {
            return GeminiRecommendationViewModel(
                songRepository = songRepository,
                profileViewModel = profileViewModel,
                geminiClient = geminiClient,
                tokenManager = tokenManager,
                userRepository = userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: \${modelClass.name}")
    }
}
