package com.example.learnical.lyrics.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnical.lyrics.network.ApiClient
import com.example.learnical.lyrics.network.LyricsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LyricsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LyricsUiState>(LyricsUiState.Idle)
    private var currentSong: String? = null
    val uiState: StateFlow<LyricsUiState> = _uiState.asStateFlow()

    fun onNewSong(song: String) {
            viewModelScope.launch {
                _uiState.value = LyricsUiState.Loading
                try {
                    val response = ApiClient.api.getLyrics(song)
                    _uiState.value = LyricsUiState.Success(response)
                    currentSong = song
                } catch (e: Exception) {
                    Log.e("ApiClient", "Exception in API call: ${e.message}", e)
                    _uiState.value = LyricsUiState.Error("Lyrics not found.")
                }
            }
    }
}

