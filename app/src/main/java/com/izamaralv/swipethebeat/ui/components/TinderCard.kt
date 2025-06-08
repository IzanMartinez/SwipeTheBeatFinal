package com.izamaralv.swipethebeat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.izamaralv.swipethebeat.common.cardBorderColor
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.ui.theme.greenBrilliantColor
import com.izamaralv.swipethebeat.ui.theme.redBrilliantColor

@Composable
fun TinderCard(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit
) {
    // Variables para el desplazamiento de la tarjeta
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Color y opacidad de la superposición dinámica
    val overlayColor = when {
        offsetX > 0 -> greenBrilliantColor.copy(alpha = minOf(0.4f, offsetX / 600))
        offsetX < 0 -> redBrilliantColor.copy(alpha = minOf(0.4f, -offsetX / 600))
        else -> Color.Transparent
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth(.8f)
            .fillMaxHeight(.65f)
            .graphicsLayer(
                translationX = offsetX,
                translationY = offsetY,
                rotationZ = offsetX / 20
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    // Detecta gestos de arrastre
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        // Reacciona según la dirección del arrastre
                        when {
                            offsetX > 300 -> onSwipeRight()
                            offsetX < -300 -> onSwipeLeft()
                        }
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            }
    ) {
        // Superposición para feedback del arrastre (siempre en la parte superior)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f) // Asegura que la superposición esté sobre el contenido de la tarjeta
                .background(overlayColor, shape = RoundedCornerShape(16.dp))
        )

        // Contenido de la tarjeta
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f) // Coloca el contenido de la tarjeta debajo de la superposición
                .background(cardColor.value, shape = RoundedCornerShape(16.dp))
                .border(3.dp, cardBorderColor.value, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}
