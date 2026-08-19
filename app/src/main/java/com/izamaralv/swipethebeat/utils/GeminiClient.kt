package com.izamaralv.swipethebeat.utils

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

object GeminiClient {

    private const val DEFAULT_MODEL = "gemini-flash-lite-latest"

    private const val MAX_RETRIES = 4
    private const val RETRY_DELAY_MS = 1200L

    data class RecommendationJson(val name: String)

    suspend fun getRecommendations(prompt: String): List<RecommendationJson> =
        withContext(Dispatchers.IO) {

            val wrapperPrompt = """
                Please respond with ONLY a JSON array of 30 song recommendation titles.
                Do NOT include any commentary or explanation, just the JSON array of names.
                Data:
                $prompt
            """.trimIndent()

            Log.d("GeminiClient", "Prompt:\n$wrapperPrompt")

            val gson = GsonBuilder().create()
            val escapedPrompt = gson.toJson(wrapperPrompt)

            val jsonBody = """
            {
              "contents": [
                {
                  "role": "user",
                  "parts": [
                    { "text": $escapedPrompt }
                  ]
                }
              ]
            }
            """.trimIndent()

            val client = OkHttpClient()

            repeat(MAX_RETRIES) { attempt ->

                try {
                    val url =
                        "https://generativelanguage.googleapis.com/v1beta/models/$DEFAULT_MODEL:generateContent?key=${Credentials.GEMINI_API_KEY}"

                    val request = Request.Builder()
                        .url(url)
                        .post(RequestBody.create("application/json".toMediaType(), jsonBody))
                        .build()

                    val response = client.newCall(request).execute()
                    val rawResponse = response.body?.string().orEmpty()

                    Log.d("GeminiClient", "Raw REST response:\n$rawResponse")

                    val root = gson.fromJson(rawResponse, Map::class.java)

                    // Manejo de errores de la API
                    if (root.containsKey("error")) {
                        val err = root["error"] as Map<*, *>
                        val message = err["message"]?.toString() ?: "Unknown error"

                        // Si es saturación o límite de cuota, reintentar
                        if (message.contains("high demand", ignoreCase = true) ||
                            message.contains("quota", ignoreCase = true)
                        ) {
                            Log.w("GeminiClient", "Retrying attempt ${attempt + 1} due to: $message")
                            delay(RETRY_DELAY_MS)
                            return@repeat
                        }

                        throw IOException("Gemini API error: $message")
                    }

                    // Extraer candidatos
                    val candidates = root["candidates"] as? List<*>
                        ?: throw IOException("Missing candidates in response: $rawResponse")

                    val content = (candidates[0] as Map<*, *>)["content"] as Map<*, *>
                    val parts = content["parts"] as List<*>
                    var raw = (parts[0] as Map<*, *>)["text"] as String

                    raw = raw.trim()

                    // Quitar comillas si viene como string literal
                    if (raw.startsWith("\"") && raw.endsWith("\"")) {
                        raw = gson.fromJson(raw, String::class.java)
                    }

                    // Quitar fences ``` si los añade el modelo
                    if (raw.startsWith("```")) {
                        raw = raw.replaceFirst("^```[a-zA-Z]*\\r?\\n".toRegex(), "")
                        raw = raw.replaceFirst("\\r?\\n```$".toRegex(), "")
                    }

                    // Validar que sea un array JSON
                    if (!raw.trimStart().startsWith("[")) {
                        throw IOException("Expected JSON array but got: ${raw.take(200)}")
                    }

                    val normalized = raw.replace(Regex(",\\s*]"), "]")
                    val listType = object : TypeToken<List<String>>() {}.type
                    val titles: List<String> = gson.fromJson(normalized, listType)

                    return@withContext titles.map { RecommendationJson(it) }
                } catch (e: Exception) {
                    Log.e("GeminiClient", "Attempt ${attempt + 1} failed:", e)

                    if (attempt == MAX_RETRIES - 1) {
                        throw IOException("Error in GeminiClient(): ${e.message}", e)
                    }

                    delay(RETRY_DELAY_MS)
                }
            }

            throw IOException("GeminiClient failed after retries")
        }
}
