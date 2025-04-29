package com.example.learnical.sever

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class ServerViewModel @Inject constructor(private val serverService: ServerService) : ViewModel() {

    private val _serverStatus = MutableStateFlow<ServerStatusUiState>(ServerStatusUiState.Loading)
    val serverStatus: StateFlow<ServerStatusUiState> = _serverStatus.asStateFlow()

    suspend fun fetchServerStatus() {
        _serverStatus.value = ServerStatusUiState.Loading
        _serverStatus.value = if (serverService.isServerRunning()) ServerStatusUiState.Up else  ServerStatusUiState.Down
    }
}