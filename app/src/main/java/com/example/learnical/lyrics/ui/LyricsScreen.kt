package com.example.learnical.lyrics.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.learnical.MainActivity
import com.example.learnical.lyrics.presentation.LyricsUiState
import com.example.learnical.lyrics.presentation.LyricsViewModel

@Composable
fun LyricsScreen(viewModel: LyricsViewModel, mainActivity: MainActivity) {
    val uiState by viewModel.uiState.collectAsState()
    var songName by remember { mutableStateOf("") }

    Spacer(modifier = Modifier.height(20.dp))
    Column(        modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {
        Button(onClick = { mainActivity.authSpotify() }) {
            Text("Auth Spotify")
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = uiState) {
            is LyricsUiState.Loading -> {
                songName = "Loading..."
                Text("Loading...")
            }
            is LyricsUiState.Success -> {
                songName = state.data.songName
                LyricsResult(state.data)
            }
            is LyricsUiState.Error -> {
                songName = "Error"
                Text(state.message, color = Color.Red)
            }
            LyricsUiState.Idle -> {
                songName = ""
            }
        }
        OutlinedTextField(
            value = songName,
            onValueChange = { /* song name will be updated by theLaunchedEffect */ },
            label = { Text("Current song") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
    }
}