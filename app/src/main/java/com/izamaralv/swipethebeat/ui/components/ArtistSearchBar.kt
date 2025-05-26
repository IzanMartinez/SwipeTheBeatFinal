package com.izamaralv.swipethebeat.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izamaralv.swipethebeat.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ArtistSearchBar(
    viewModel: SearchViewModel,
    accessToken: String,
    onArtistSelected: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val artists by viewModel.artistResults
    var expanded by remember { mutableStateOf(false) }

    // container that anchors the dropdown automatically
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.searchArtists(it, accessToken)
                expanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
            placeholder = { Text("Type artist name…", color = Color.LightGray) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Magenta,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.Magenta
            )
        )

        ExposedDropdownMenu(
            expanded = expanded && artists.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            // optional header
            DropdownMenuItem(onClick = {}, enabled = false) {
                Text("Found ${artists.size} artists", color = Color.White)
            }
            artists.forEach { artist ->
                Divider(color = Color.LightGray, thickness = 0.5.dp)
                DropdownMenuItem(onClick = {
                    expanded = false
                    onArtistSelected(artist)
                    query = artist
                }) {
                    Text(artist, color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }

    LaunchedEffect(artists) {
        Log.d("ArtistSearchBar", "🎯 Dropdown recomposed – ${artists.size} results")
    }
}


