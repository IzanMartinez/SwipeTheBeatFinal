package com.izamaralv.swipethebeat.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izamaralv.swipethebeat.common.softComponentColor
import com.izamaralv.swipethebeat.viewmodel.SearchViewModel

@Composable
fun ArtistSearchBar(viewModel: SearchViewModel, accessToken: String, onArtistSelected: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    val artists by viewModel.artistResults // ✅ Observe search results correctly
    var isFocused by remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        TextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.searchArtists(query, accessToken) // ✅ Trigger API call
            },
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                .onFocusChanged { focusState -> isFocused = focusState.isFocused }, // ✅ Correct way to track focus
            singleLine = true,
            textStyle = TextStyle(fontSize = 24.sp, color = softComponentColor.value)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            if (artists.isEmpty()) { // ✅ Show placeholders if the list is empty
                items(5) { index ->
                    Text(
                        text = "Placeholder Item $index",
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        style = TextStyle(fontSize = 20.sp, color = softComponentColor.value)
                    )
                }
            } else {
                items(artists) { artist ->
                    Text(
                        text = artist,
                        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onArtistSelected(artist) }
                    )
                }
            }
        }




    }
    Log.d("ArtistSearchBar", "🔥 Displaying artists: $artists")

}
