package com.izamaralv.swipethebeat.models

import com.google.gson.annotations.SerializedName

data class LikedSongsResponse(
    @SerializedName("items") val tracks: List<LikedSongItem>
)

data class LikedSongItem(
    @SerializedName("track") val track: Track
)
