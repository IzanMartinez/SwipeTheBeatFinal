package com.izamaralv.swipethebeat.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.models.Track
import com.izamaralv.swipethebeat.viewmodel.GeminiRecommendationViewModel.RecommendationState

@Composable
fun RecommendationsSection(
    state: RecommendationState,
    onSave: () -> Unit,
    onLike: (Track) -> Unit,
    onDislike: () -> Unit
) {
    when (state) {
        is RecommendationState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is RecommendationState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.message,
                    color = Color.Red
                )
            }
        }
        is RecommendationState.Success -> {
            val list = state.recommendations
            if (list.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay recomendaciones", color = softComponentColor.value)
                }
            } else {
                val current = list.first()
                RecommendationCard(
                    track = current,
                    onSave = onSave,
                    onLike = { onLike(current) },
                    onDislike = onDislike

                )
            }
        }
    }
}
