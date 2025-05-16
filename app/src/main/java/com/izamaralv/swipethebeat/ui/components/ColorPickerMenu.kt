package com.izamaralv.swipethebeat.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.ui.theme.bluePastelColor
import com.izamaralv.swipethebeat.ui.theme.greenPastelColor
import com.izamaralv.swipethebeat.ui.theme.orangePastelColor
import com.izamaralv.swipethebeat.ui.theme.pinkPastelColor
import com.izamaralv.swipethebeat.ui.theme.purplePastelColor
import com.izamaralv.swipethebeat.ui.theme.redPastelColor
import com.izamaralv.swipethebeat.utils.changeColor
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

@Composable
fun ColorPickerMenu(
    profileViewModel: ProfileViewModel,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = true, // Controlled externally
        onDismissRequest = onDismiss,
        modifier = Modifier.background(color = cardColor.value)
    ) {
        val colorOptions = listOf(
            Triple("Green", greenPastelColor, R.drawable.green),
            Triple("Orange", orangePastelColor, R.drawable.orange),
            Triple("Blue", bluePastelColor, R.drawable.blue),
            Triple("Red", redPastelColor, R.drawable.red),
            Triple("Purple", purplePastelColor, R.drawable.purple),
            Triple("Pink", pinkPastelColor, R.drawable.pink)
        )


        colorOptions.forEach { (colorName, colorValue, drawableId) ->
            DropdownMenuItem(
                leadingIcon = {
                    Image(
                        painter = painterResource(drawableId),
                        contentDescription = "$colorName color",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                },
                text = { Text(colorName, color = colorValue) },
                onClick = {
                    changeColor(colorValue, profileViewModel.getUserId(), profileViewModel)
                    onDismiss()
                },
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

    }
}
