package com.izamaralv.swipethebeat.models

data class User(
    val user_id: String,
    val email: String,
    val nombre: String,
    val admin: Boolean = false,
    val spotify_token: String? = null
) {
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "user_id" to this.user_id,
            "email" to this.email,
            "nombre" to this.nombre,
            "admin" to this.admin,
            "spotify_token" to (this.spotify_token ?: "")
        )
    }
}
