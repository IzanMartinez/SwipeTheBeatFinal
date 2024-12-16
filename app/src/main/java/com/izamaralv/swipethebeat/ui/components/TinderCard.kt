package com.izamaralv.swipethebeat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.ui.theme.greenPastelColor
import com.izamaralv.swipethebeat.ui.theme.redPastelColor

@Composable
fun TinderCard(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Dynamic overlay color and opacity
    val overlayColor = when {
        offsetX > 0 -> greenPastelColor.copy(alpha = minOf(0.4f, offsetX / 600))
        offsetX < 0 -> redPastelColor.copy(alpha = minOf(0.4f, -offsetX / 600))
        else -> Color.Transparent
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth(.8f)
            .fillMaxHeight(.55f)
//            .aspectRatio(0.75f) // Slimmer card aspect ratio
            .graphicsLayer(
                translationX = offsetX,
                translationY = offsetY,
                rotationZ = offsetX / 20
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
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
        // Overlay for swipe feedback (always on top)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f) // Ensures overlay is above the card content
                .background(overlayColor, shape = RoundedCornerShape(16.dp))
        )

        // Card content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f) // Places card content below the overlay
                .background(cardColor.value, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}
