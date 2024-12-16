package com.izamaralv.swipethebeat.models

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("tracks") val tracks: TrackList
)

data class TrackList(
    @SerializedName("items") val items: List<Track>
)

data class Track(
//    val id: String,
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val preview_url: String?,
    val id: String,
    val uri: String
) {
}

data class Artist(
    val id: String,
    val name: String
)

data class Album(
    val id: String,
    val name: String,
    val images: List<Image>
)

data class Image(
    val url: String
)
