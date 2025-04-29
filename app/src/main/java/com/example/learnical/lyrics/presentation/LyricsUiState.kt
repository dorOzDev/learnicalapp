package com.example.learnical.lyrics.presentation

import com.example.learnical.lyrics.network.LyricsResponse

sealed class LyricsUiState {
    object Idle : LyricsUiState()
    object Loading : LyricsUiState()
    data class Success(val data: LyricsResponse) : LyricsUiState()
    data class Error(val message: String) : LyricsUiState()
}