package com.izamaralv.swipethebeat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izamaralv.swipethebeat.models.Track
import com.izamaralv.swipethebeat.repository.SongRepository
import com.izamaralv.swipethebeat.repository.UserRepository
import com.izamaralv.swipethebeat.utils.GeminiClient
import com.izamaralv.swipethebeat.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeminiRecommendationViewModel(
    private val songRepository: SongRepository,
    private val profileViewModel: ProfileViewModel,
    private val geminiClient: GeminiClient,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        private const val REFILL_THRESHOLD = 10
        private const val REFILL_BATCH_SIZE = 30
    }

    sealed class RecommendationState {
        object Loading : RecommendationState()
        data class Success(val recommendations: List<Track>) : RecommendationState()
        data class Error(val message: String) : RecommendationState()
    }

    private val _state = MutableStateFlow<RecommendationState>(RecommendationState.Loading)
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    /**
     * Carga inicial o recarga completa de recomendaciones.
     */
    fun loadRecommendations() {
        viewModelScope.launch {
            _state.value = RecommendationState.Loading
            try {
                val userId = profileViewModel.getUserId()
                val token  = tokenManager.getAccessToken().orEmpty()

                // 1) Cargar JSON guardado
                val savedJson = userRepository.loadRecommendationsSuspend(userId)
                // 2) Resolver cada título en Track
                val savedTracks = savedJson.mapNotNull { rec ->
                    songRepository.searchExactTrack(token, rec["name"].orEmpty())
                }
                _state.value = RecommendationState.Success(savedTracks)

                // 3) Refill si bajo umbral
                if (savedTracks.size < REFILL_THRESHOLD) {
                    refillRecommendations()
                }
            } catch (e: Exception) {
                _state.value = RecommendationState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    /**
     * Añade más recomendaciones cuando el buffer baja de umbral.
     */
    private fun refillRecommendations() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getAccessToken().orEmpty()
                val userId = profileViewModel.getUserId()

                // 1) Favoritos y últimos likes
                val favorites = listOf(
                    profileViewModel.favoriteArtist1,
                    profileViewModel.favoriteArtist2,
                    profileViewModel.favoriteArtist3
                ).filter { it.isNotBlank() }
                val likedTracks = songRepository.getLast50LikedSongs(token)
                val likedIds = likedTracks.map { it.id }.toSet()

                // 2) Solicitar nuevas recomendaciones
                val titlesJson = geminiClient.getRecommendations(buildGeminiPrompt(favorites, likedTracks))
                val newTracks = titlesJson.mapNotNull { rec ->
                    songRepository.searchExactTrack(token, rec.name)
                }

                // 3) Cargar existentes y combinar
                val savedJson = userRepository.loadRecommendationsSuspend(userId)
                val existing = savedJson.mapNotNull { rec ->
                    songRepository.searchExactTrack(token, rec["name"].orEmpty())
                }
                val combined = (existing + newTracks)
                    .distinctBy { it.id }
                    .filterNot { it.id in likedIds }

                // 4) Guardar en Firestore
                userRepository.saveRecommendations(
                    userId,
                    combined.map { mapOf("name" to "${it.name} - ${it.artists.firstOrNull()?.name}") }
                )

                // 5) Emitir nuevo estado
                _state.value = RecommendationState.Success(combined)
            } catch (e: Exception) {
                _state.value = RecommendationState.Error(e.localizedMessage ?: "Error en refill")
            }
        }
    }

    /**
     * Marca una canción en Spotify como favorita.
     */
    fun likeSongInSpotify(trackId: String) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getAccessToken().orEmpty()
                songRepository.likeTrack(token, trackId)
            } catch (e: Exception) {
                Log.e("GeminiRecommendationVM", "Error liking in Spotify: ${e.message}")
            }
        }
    }

    /**
     * Consume la primera canción del buffer y actualiza Firestore/estado.
     */
    fun onSongSwiped() {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState is RecommendationState.Success) {
                val currentList = currentState.recommendations.toMutableList()
                if (currentList.isNotEmpty()) {
                    currentList.removeAt(0)
                    _state.value = RecommendationState.Success(currentList)
                    // Guardar cola actualizada
                    val userId = profileViewModel.getUserId()
                    userRepository.saveRecommendations(
                        userId,
                        currentList.map { track ->
                            mapOf("name" to "${track.name} - ${track.artists.firstOrNull()?.name}")
                        }
                    )
                    // Refill si baja umbral
                    if (currentList.size < REFILL_THRESHOLD) {
                        refillRecommendations()
                    }
                }
            }
        }
    }

    private fun buildGeminiPrompt(
        favorites: List<String>,
        likedTracks: List<Track>
    ): String {
        val favsText = if (favorites.isNotEmpty()) favorites.joinToString(", ") else "none"
        val likesText = if (likedTracks.isNotEmpty()) {
            likedTracks.joinToString(" | ") { it.name }
        } else "no recent liked songs"
        return """
            These are the top favorite artists:
            $favsText

            These are the last ${likedTracks.size} liked songs:
            $likesText

            Please respond with ONLY a JSON array of $REFILL_BATCH_SIZE song titles in the format "Title - Artist".
            Do NOT include any commentary, explanation, or metadata—only the array of strings.
        """.trimIndent()
    }
}
