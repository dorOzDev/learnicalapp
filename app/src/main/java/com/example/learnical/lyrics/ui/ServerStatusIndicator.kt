package com.example.learnical.lyrics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnical.sever.ServerStatusUiState

@Composable
fun ServerStatusIndicator(serverUiState: ServerStatusUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val color = when (serverUiState) {
            ServerStatusUiState.Down -> Color.Red
            ServerStatusUiState.Loading -> Color.Gray
            ServerStatusUiState.Up -> Color.Green
        }
        val status = when (serverUiState) {
            ServerStatusUiState.Down -> "Down"
            ServerStatusUiState.Loading -> "Loading"
            ServerStatusUiState.Up -> "Up"
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Server Status", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = status, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Spacer(
            modifier = Modifier
                .size(16.dp)
                .background(color, CircleShape)
        )
    }
}