package com.izamaralv.swipethebeat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izamaralv.swipethebeat.models.Track
import com.izamaralv.swipethebeat.repository.SongRepository
import kotlinx.coroutines.launch
import android.util.Log

class SongViewModel(private val songRepository: SongRepository, private val accessToken: String) : ViewModel() {

    private val _currentSong = MutableLiveData<Track?>()
    val currentSong: MutableLiveData<Track?> get() = _currentSong

    private val likedSongsPool = mutableListOf<Track>()
    private val likedSongIds = mutableSetOf<String>()

    init {
        loadInitialRecommendations()
    }

    fun loadInitialRecommendations() {
        viewModelScope.launch {
            try {
                likedSongsPool.clear()
                likedSongIds.clear()
                val lastLikedSongs = songRepository.getLast50LikedSongs(accessToken)
                Log.d("SongViewModel", "Loaded liked songs: ${lastLikedSongs.map { it.name }}")
                likedSongsPool.addAll(lastLikedSongs)
                likedSongIds.addAll(lastLikedSongs.map { it.id })
                searchNextTrack()
            } catch (e: Exception) {
                Log.e("SongViewModel", "Failed to load initial recommendations: ${e.message}")
            }
        }
    }

    private fun searchNextTrack() {
        if (likedSongsPool.isNotEmpty()) {
            val randomLikedSong = likedSongsPool.random()
            searchTracks(randomLikedSong.name)
        } else {
            Log.e("SongViewModel", "No liked songs available for searching")
        }
    }

    private fun searchTracks(query: String) {
        viewModelScope.launch {
            try {
                Log.d("SongViewModel", "Searching tracks with query: $query")
                val searchResults = songRepository.searchSimilarTracks(accessToken, query)
                val filteredResults = searchResults.filterNot { likedSongIds.contains(it.id) }
                if (filteredResults.isNotEmpty()) {
                    val randomResult = filteredResults.random()
                    _currentSong.value = randomResult
                } else {
                    Log.d("SongViewModel", "No new results found, searching next liked song")
                    searchNextTrack()
                }
            } catch (e: Exception) {
                Log.e("SongViewModel", "Failed to search tracks: ${e.message}")
            }
        }
    }

    fun likeCurrentSong() {
        _currentSong.value?.let { song ->
            viewModelScope.launch {
                try {
                    songRepository.likeTrack(accessToken, song.id) // Add this line to like the song on Spotify
                    addToPool(song)
                    searchNextTrack()
                } catch (e: Exception) {
                    Log.e("SongViewModel", "Failed to like the song: ${e.message}")
                }
            }
        }
    }


    fun dislikeCurrentSong() {
        searchNextTrack()
    }

    private fun addToPool(song: Track) {
        likedSongsPool.add(song)
        likedSongIds.add(song.id)
        Log.d("SongViewModel", "Adding song to pool: ${song.name}")
    }
}
