package com.izamaralv.swipethebeat.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izamaralv.swipethebeat.models.SongDTO
import com.izamaralv.swipethebeat.repository.SongRepository
import kotlinx.coroutines.launch
import android.util.Log

class SongViewModel(private val songRepository: SongRepository) : ViewModel() {

    private val _recommendedSongs = mutableStateOf<List<SongDTO>?>(null)
    val recommendedSongs: State<List<SongDTO>?> = _recommendedSongs

    init {
        loadRecommendations()
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            try {
                _recommendedSongs.value = songRepository.getRecommendations()
                Log.d("SongViewModel", "Recommendations loaded: ${_recommendedSongs.value}")
            } catch (e: Exception) {
                Log.e("SongViewModel", "Failed to load recommendations: ${e.message}")
            }
        }
    }
}
