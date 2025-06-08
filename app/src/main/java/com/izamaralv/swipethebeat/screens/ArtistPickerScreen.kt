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
    currentFavorite: String,
    excluded: List<String>,
    onArtistSelected: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val artists by searchViewModel.artistResults

    val context = LocalContext.current
    val accessToken = TokenManager(context).getAccessToken().orEmpty()

    // Al entrar, limpiamos resultados anteriores
    LaunchedEffect(Unit) {
        searchViewModel.clearArtistResults()
    }

    // Quitamos de la lista los ya seleccionados
    val filtered = artists.filterNot { it in excluded }

    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor.value)
            .padding(16.dp)
    ) {
        // Barra de búsqueda
        TextField(
            value = query,
            onValueChange = {
                query = it
                searchViewModel.searchArtists(it, accessToken)
            },
            placeholder = {
                Text(
                    "Busca un artista...",
                    color = textColor.value.copy(alpha = 0.6f)
                )
            },
            singleLine = true,
            textStyle = TextStyle(color = textColor.value, fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, softComponentColor.value, RoundedCornerShape(20.dp))
                .background(cardColor.value)
        )

        Spacer(Modifier.height(12.dp))

        when {
            query.isBlank() -> {
                // Opción para quitar favorito si ya existe uno
                if (currentFavorite.isNotBlank()) {
                    ListItem(
                        text = { Text("Quitar artista favorito", color = textColor.value) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onArtistSelected("") }
                    )
                    Divider(
                        color = softComponentColor.value,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Text(
                    text = "Empieza a escribir para ver resultados",
                    color = textColor.value,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }

            query.isNotBlank() && filtered.isEmpty() -> {
                Text(
                    "No se han encontrado artistas",
                    color = textColor.value,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
            else -> {
                // Mostramos lista de resultados
                LazyColumn(Modifier.fillMaxSize()) {
                    items(filtered) { artist ->
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
}



