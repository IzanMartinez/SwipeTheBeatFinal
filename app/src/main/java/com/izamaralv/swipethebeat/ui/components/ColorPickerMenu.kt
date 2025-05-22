package com.izamaralv.swipethebeat.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.izamaralv.swipethebeat.R
import com.izamaralv.swipethebeat.ui.theme.bluePastelColor
import com.izamaralv.swipethebeat.ui.theme.greenPastelColor
import com.izamaralv.swipethebeat.ui.theme.orangePastelColor
import com.izamaralv.swipethebeat.ui.theme.pinkPastelColor
import com.izamaralv.swipethebeat.ui.theme.purplePastelColor
import com.izamaralv.swipethebeat.ui.theme.redPastelColor
import com.izamaralv.swipethebeat.ui.theme.yellowHighContrastColor
import com.izamaralv.swipethebeat.utils.changeColor
import com.izamaralv.swipethebeat.viewmodel.ProfileViewModel

@Composable
fun ColorPickerMenu(profileViewModel: ProfileViewModel) {
    val colorOptions = listOf(
        Pair(greenPastelColor, R.drawable.green),
        Pair(orangePastelColor, R.drawable.orange),
        Pair(bluePastelColor, R.drawable.blue),
        Pair(redPastelColor, R.drawable.red),
        Pair(purplePastelColor, R.drawable.purple),
        Pair(pinkPastelColor, R.drawable.pink),
        Pair(yellowHighContrastColor, R.drawable.black)
    )

    Box(

    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items(colorOptions) { (colorValue, drawableId) ->
                Image(
                    painter = painterResource(drawableId),
                    contentDescription = "Color option",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            changeColor(
                                colorValue,
                                profileViewModel.getUserId(),
                                profileViewModel
                            )
                        }
                        .background(colorValue)
                        .then(
                            if (colorValue == yellowHighContrastColor) Modifier.border(
                                2.dp,
                                yellowHighContrastColor,
                                CircleShape
                            ) else Modifier
                        ), // ✅ Add border only if it's yellowHighContrastColor
                    contentScale = ContentScale.Crop
                )
            }
        }

    }
}
