package com.izamaralv.swipethebeat.utils

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Cliente para generar recomendaciones de canciones usando Gemini (Generative AI SDK).
 */
object GeminiClient {

    // Modelo predeterminado de tu proyecto AI Studio
    private const val DEFAULT_MODEL = "gemini-2.0-flash"

    /**
     * Representa solo el título devuelto.
     */
    data class RecommendationJson(
        val name: String
    )

    /**
     * Envía el prompt y obtiene un JSON array de títulos de canción.
     */
    suspend fun getRecommendations(
        prompt: String,
    ): List<RecommendationJson> = withContext(Dispatchers.IO) {
        // Envolvemos prompt en instrucciones estrictas de solo JSON
        val wrapperPrompt = """
            Please respond with ONLY a JSON array of 30 song recommendation titles.
            Do NOT include any commentary or explanation, just the JSON array of names.
            Data:
            $prompt
        """.trimIndent()

        Log.d("GeminiClient", "Prompt:\n$wrapperPrompt")

        try {
            // Inicializamos el cliente
            val model = GenerativeModel(
                modelName = DEFAULT_MODEL,
                apiKey     = Credentials.GEMINI_API_KEY
            )

            // Llamada síncrona para asegurar contenido completo
            val response: GenerateContentResponse = model.generateContent(wrapperPrompt)

            // Texto bruto
            var raw = response.text?.trim().orEmpty()
            Log.d("GeminiClient", "Raw response:\n$raw")

            // Si está entre comillas dobles, lo desempaquetamos
            if (raw.startsWith("\"") && raw.endsWith("\"")) {
                raw = GsonBuilder().create().fromJson(raw, String::class.java)
                Log.d("GeminiClient", "Unwrapped JSON array:\n$raw")
            }

            // Eliminamos las fences Markdown (``` o ```json)
            if (raw.startsWith("```") ) {
                // Quita la línea de apertura con posible etiqueta de lenguaje
                raw = raw.replaceFirst("^```[a-zA-Z]*\\r?\\n".toRegex(), "")
                // Quita la línea de cierre
                raw = raw.replaceFirst("\\r?\\n```$".toRegex(), "")
                Log.d("GeminiClient", "Cleaned fences, JSON array:\n$raw")
            }

            // Nos aseguramos de que empieza con '['
            if (!raw.trimStart().startsWith("[")) {
                throw IOException("Expected JSON array but got: ${raw.take(200)}")
            }

            // Normalizamos y parseamos con lenient
            val normalized = raw.replace(Regex(",\\s*]"), "]")
            val gson = GsonBuilder().setLenient().create()
            val listType = object : TypeToken<List<String>>() {}.type
            val titles: List<String> = gson.fromJson(normalized, listType)

            // Mapeamos a RecommendationJson
            return@withContext titles.map { RecommendationJson(it) }

        } catch (e: Exception) {
            Log.e("GeminiClient", "Error fetching recommendations:", e)
            throw IOException("Error in GeminiClient(): ${e.message}", e)
        }
    }
}
