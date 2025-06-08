package com.izamaralv.swipethebeat.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izamaralv.swipethebeat.repository.SongRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val songRepository: SongRepository) : ViewModel() {
    private val _artistResults = mutableStateOf<List<String>>(emptyList())
    val artistResults: State<List<String>> = _artistResults

    fun searchArtists(query: String, token: String) {
        viewModelScope.launch {
            val results = songRepository.searchArtists(token, query)
            Log.d("SearchViewModel", "✅ API Response: $results")
            _artistResults.value = results
        }
    }

    fun clearArtistResults() {
        _artistResults.value = emptyList()
    }

}
