package com.izamaralv.swipethebeat.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izamaralv.swipethebeat.common.backgroundColor
import com.izamaralv.swipethebeat.common.cardColor
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.common.textColor
import com.izamaralv.swipethebeat.utils.TokenManager
import com.izamaralv.swipethebeat.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ArtistPickerScreen(
    searchViewModel: SearchViewModel,
    onArtistSelected: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val artists by searchViewModel.artistResults

    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken()

    LaunchedEffect(Unit) {
        searchViewModel.clearArtistResults()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(color = backgroundColor.value)
            .padding(16.dp)
    ) {
        TextField(
            value = query,
            onValueChange = {
                query = it
                searchViewModel.searchArtists(it, accessToken.toString())
            },
            placeholder = {
                Text(
                    text = "Busca un artista...",
                    color = textColor.value.copy(alpha = 0.6f)
                )
            },
            singleLine = true,
            textStyle = TextStyle(color = textColor.value, fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    2.dp,
                    softComponentColor.value,
                    RoundedCornerShape(20.dp)
                ) // Adds a white border
                .background(cardColor.value)
//                .padding(horizontal = 16.dp, vertical = 12.dp), // Keeps the background color,
        )

        Spacer(Modifier.height(12.dp))

        if (artists.isEmpty()) {
            Text(
                text = "Empieza a escribir para ver resultados",
                color = textColor.value,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(artists) { artist ->
                    ListItem(
                        text = { Text(artist, color = textColor.value) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onArtistSelected(artist) }
                    )
                    Divider(
                        color = softComponentColor.value,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
        }
    }
}

