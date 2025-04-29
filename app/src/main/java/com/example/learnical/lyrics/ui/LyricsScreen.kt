package com.example.learnical.lyrics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learnical.lyrics.presentation.LyricsUiState
import com.example.learnical.lyrics.presentation.LyricsViewModel
import com.example.learnical.sever.ServerViewModel

@Composable
fun LyricsScreen(
    lyricViewModel: LyricsViewModel,
    serverViewModel: ServerViewModel,
    onAuthSpotifyClicked: () -> Unit
) {
    val uiState by lyricViewModel.uiState.collectAsState()
    val serverUiState by serverViewModel.serverStatus.collectAsState()

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {
        ServerStatusIndicator(serverUiState = serverUiState)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { onAuthSpotifyClicked() }) {
            Text("Auth Spotify")
        }
        LyricsContent(uiState)
    }
}

@Composable
fun LyricsContent(uiState: LyricsUiState){
    when (uiState) {
        is LyricsUiState.Error -> {
            Text(text = "Error: ${uiState.message}")
        }

        LyricsUiState.Idle -> {
            Text(text = "Search for lyrics")
        }

        LyricsUiState.Loading -> {
            Text(text = "Loading lyrics...")
        }

        is LyricsUiState.Success -> {
            Text(text = uiState.data.lyric)
        }
    }
}