package com.example.learnical.sever

sealed class ServerStatusUiState {
    object Loading : ServerStatusUiState()
    object Up : ServerStatusUiState()
    object Down : ServerStatusUiState()
}