package com.izamaralv.swipethebeat.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izamaralv.swipethebeat.models.Track
import com.izamaralv.swipethebeat.repository.SongRepository
import kotlinx.coroutines.launch

class SongViewModel(private val songRepository: SongRepository, private val accessToken: String) : ViewModel() {

    // Propiedad mutable que contiene la canción actual
    private val _currentSong = MutableLiveData<Track?>()
    val currentSong: LiveData<Track?> get() = _currentSong

    // Propiedad mutable que contiene la lista de canciones favoritas
    private val _likedSongsPool = MutableLiveData<List<Track>>()
    val likedSongsPool: LiveData<List<Track>> get() = _likedSongsPool
    private val likedSongIds = mutableSetOf<String>()

    // Inicializa las recomendaciones iniciales
    init {
        if (accessToken.isNotBlank()) {
            viewModelScope.launch {
                loadInitialRecommendationsInternal()
            }
        }
    }

    // Carga las recomendaciones iniciales
    fun loadInitialRecommendationsInternal() {
        viewModelScope.launch {
            try {
                likedSongIds.clear()
                val lastLikedSongs = songRepository.getLast50LikedSongs(accessToken)
                Log.d("SongViewModel", "Loaded liked songs: ${lastLikedSongs.map { it.name }}")
                _likedSongsPool.value = lastLikedSongs
                likedSongIds.addAll(lastLikedSongs.map { it.id })
                searchNextTrack()
            } catch (e: Exception) {
                Log.e("SongViewModel", "Failed to load initial recommendations: ${e.message}")
            }
        }
    }

    // Busca la siguiente canción
    private fun searchNextTrack() {
        val likedSongs = _likedSongsPool.value ?: emptyList()
        if (likedSongs.isNotEmpty()) {
            val randomLikedSong = likedSongs.random()
            searchTracks(randomLikedSong.name)
        } else {
            Log.e("SongViewModel", "No liked songs available for searching")
        }
    }

    // Realiza una búsqueda de canciones similares
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

    // Marca la canción actual como favorita
    fun likeCurrentSong() {
        _currentSong.value?.let { song ->
            viewModelScope.launch {
                try {
                    // Añade esta línea para marcar la canción como favorita en Spotify
                    songRepository.likeTrack(accessToken, song.id)
                    addToPool(song)
                    searchNextTrack()
                } catch (e: Exception) {
                    Log.e("SongViewModel", "Failed to like the song: ${e.message}")
                }
            }
        }
    }

    /**
     * Marca la canción con el ID dado como favorita en Spotify,
     * sin depender de `_currentSong`.
     */
    fun likeSongById(trackId: String) {
        viewModelScope.launch {
            try {
                // Llamamos a la API de Spotify con ese trackId
                songRepository.likeTrack(accessToken, trackId)
                Log.d("SongViewModel", "✅ likeSongById($trackId) enviado a Spotify")
            } catch (e: Exception) {
                Log.e("SongViewModel", "❌ Error en likeSongById($trackId): ${e.message}")
            }
        }
    }


    // Omite la canción actual
    fun dislikeCurrentSong() {
        searchNextTrack()
    }

    // Añade la canción al pool de canciones favoritas
    private fun addToPool(song: Track) {
        val updatedLikedSongs = _likedSongsPool.value.orEmpty().toMutableList()
        updatedLikedSongs.add(song)
        _likedSongsPool.value = updatedLikedSongs
        likedSongIds.add(song.id)
        Log.d("SongViewModel", "Adding song to pool: ${song.name}")
    }

    // Omite una canción específica
    fun dislikeCurrentSong(trackId: String) {
        viewModelScope.launch {
            try {
                songRepository.dislikeTrack(accessToken, trackId)
                // Actualiza el pool de canciones favoritas eliminando la canción omitida
                _likedSongsPool.value = _likedSongsPool.value?.filterNot { it.id == trackId }
            } catch (e: Exception) {
                Log.e("SongViewModel", "Failed to dislike the song: ${e.message}")
            }
        }
    }
}
