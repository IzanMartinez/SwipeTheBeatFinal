package com.izamaralv.swipethebeat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izamaralv.swipethebeat.models.Track
import com.izamaralv.swipethebeat.repository.SongRepository
import kotlinx.coroutines.launch
import android.util.Log

class SongViewModel(
    private val songRepository: SongRepository,
    private val accessToken: String
) : ViewModel() {

    private val _currentSong = MutableLiveData<Track?>()
    val currentSong: LiveData<Track?> = _currentSong

    private val _likedSongs = MutableLiveData<List<Track>>()
    val likedSongs: LiveData<List<Track>> = _likedSongs

    // Load initial recommendations, ensuring preview_url is included
    fun loadInitialRecommendations() {
        viewModelScope.launch {
            val recommendations = songRepository.getLast50LikedSongs(accessToken)
            val firstSong = recommendations.firstOrNull()
            if (firstSong != null) {
                _currentSong.value = firstSong
            } else {
                _currentSong.value = null // Handle no recommendations
            }
        }
    }

    // Like current song and load the next one
    fun likeCurrentSong() {
        viewModelScope.launch {
            val current = _currentSong.value
            if (current != null) {
                _likedSongs.value = _likedSongs.value.orEmpty() + current
                loadNextSong()
            }
        }
    }

    // Dislike current song and load the next one
    fun dislikeCurrentSong() {
        viewModelScope.launch {
            loadNextSong()
        }
    }

    // Load the next song recommendation
    private fun loadNextSong() {
        viewModelScope.launch {
            val recommendations = songRepository.getLast50LikedSongs(accessToken)
            val nextSong = recommendations.firstOrNull { it != _currentSong.value }
            if (nextSong != null) {
                _currentSong.value = nextSong
            } else {
                _currentSong.value = null // Handle end of recommendations
            }
        }
    }
}
