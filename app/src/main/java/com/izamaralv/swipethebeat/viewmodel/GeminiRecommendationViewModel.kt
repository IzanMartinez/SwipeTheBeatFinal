package com.izamaralv.swipethebeat.viewmodel

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izamaralv.swipethebeat.models.Track
import com.izamaralv.swipethebeat.repository.SongRepository
import com.izamaralv.swipethebeat.utils.GeminiClient
import com.izamaralv.swipethebeat.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GeminiRecommendationViewModel(
    private val songRepository: SongRepository,
    private val profileViewModel: ProfileViewModel,
    private val geminiClient: GeminiClient,
    private val tokenManager: TokenManager
) : ViewModel() {

    sealed class RecommendationState {
        object Loading : RecommendationState()
        data class Success(val recommendations: List<Track>) : RecommendationState()
        data class Error(val message: String) : RecommendationState()
    }

    private val _state = MutableStateFlow<RecommendationState>(RecommendationState.Loading)
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    /**
     * Inicia la carga de recomendaciones usando Gemini + búsqueda en Spotify,
     * filtrando fuera pistas ya "liked".
     */
    fun loadRecommendations() {
        viewModelScope.launch {
            Log.d("GeminiRecommendationVM", "loadRecommendations() called")
            _state.value = RecommendationState.Loading

            try {
                // 1) Token Spotify
                val token = tokenManager.getAccessToken().orEmpty()
                Log.d("GeminiRecommendationVM", "Spotify token: $token")

                // 2) Carga favoritos (esperar si aún no están)
                val rawFavs = listOf(
                    profileViewModel.favoriteArtist1,
                    profileViewModel.favoriteArtist2,
                    profileViewModel.favoriteArtist3
                ).filter { it.isNotBlank() }
                val favorites = if (rawFavs.isEmpty()) {
                    snapshotFlow {
                        listOf(
                            profileViewModel.favoriteArtist1,
                            profileViewModel.favoriteArtist2,
                            profileViewModel.favoriteArtist3
                        ).filter { it.isNotBlank() }
                    }
                        .filter { it.isNotEmpty() }
                        .first()
                } else rawFavs
                Log.d("GeminiRecommendationVM", "Favorite artists: $favorites")

                // 3) Últimas 50 liked
                val likedTracks = songRepository.getLast50LikedSongs(token)
                Log.d("GeminiRecommendationVM", "Retrieved ${likedTracks.size} liked tracks")
                val likedIds = likedTracks.map { it.id }.toSet()

                // 4) Construir prompt
                val prompt = buildGeminiPrompt(favorites, likedTracks)
                Log.d("GeminiRecommendationVM", "Prompt:\n$prompt")

                // 5) Llamar a Gemini (títulos con artista)
                Log.d("GeminiRecommendationVM", "Calling GeminiClient.getRecommendations()")
                val titles = geminiClient.getRecommendations(prompt)
                Log.d("GeminiRecommendationVM", "Gemini returned ${titles.size} titles")

                // 6) Buscar en Spotify cada título y filtrar liked
                val recommendations = titles.mapNotNull { rec ->
                    // rec.name == "Título - Artista"
                    val parts = rec.name.split(" - ")
                    val titlePart = parts.getOrNull(0)?.trim().orEmpty()
                    val artistPart = parts.getOrNull(1)?.trim().orEmpty()

                    // Construir query precisa usando título y artista
                    val query = "track:\"$titlePart\" artist:\"$artistPart\""

                    // Ejecutar búsqueda en Spotify
                    val results = songRepository.searchSimilarTracks(token, query)

                    // Tomar el primer Track que coincida título + artista
                    results.firstOrNull { track ->
                        track.name.equals(titlePart, ignoreCase = true) &&
                                track.artists.any { it.name.equals(artistPart, ignoreCase = true) }
                    }
                }
                    // 7) Filtrar canciones ya liked
                    .filterNot { it.id in likedIds }

                Log.d("GeminiRecommendationVM", "Filtered recommendations size: ${recommendations.size}")
                _state.value = RecommendationState.Success(recommendations)

            } catch (e: Exception) {
                Log.e("GeminiRecommendationVM", "Error in loadRecommendations()", e)
                _state.value = RecommendationState.Error(e.localizedMessage ?: "Error desconocido")
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
        } else {
            "no recent liked songs"
        }
        return """
            These are the top favorite artists:
            $favsText

            These are the last 50 liked songs:
            $likesText

            Please respond with ONLY a JSON array of 30 song titles in the format "Title - Artist".
            Do NOT include any commentary, explanation, or metadata—only the array of strings.
        """.trimIndent()
    }
}
