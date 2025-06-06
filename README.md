Para el funcionamiento de la aplicación es necesario disponer de API keys tanto de Spotify (cliente y secreta) como de Gemini.

La manera más sencilla de hacerlo funcionar si se cumplen los anteriores requisitos es añadir un objeto "Credentials.kt" en la siguiente ruta: app/src/main/java/com/izamaralv/swipethebeat/utils

debemos completarlo con algo similar a lo siguiente:


object Credentials {
    const val GEMINI_API_KEY="TU-API-KEY"
    const val SPOTIFY_CLIENT_ID="TU-API-KEY"
    const val SPOTIFY_CLIENT_SECRET="TU-API-KEY"
    const val REDIRECT_URI = "myapp://callback"
}

Es importante mantener los nombres de las constantes para que puedan llamarse desde el resto de la aplicación.
