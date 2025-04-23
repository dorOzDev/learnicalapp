package com.example.learnical

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope


// --- Entry Point ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LyricsScreen()
            }
        }
    }
}
interface LyricsApi {
    @GET("search?")
    suspend fun getLyrics(@Query("song_name") songName: String): LyricsResponse
}

data class LyricsResponse(
    val songName: String,
    val url: String,
    val lyric: String
)

object ApiClient {
    private const val BASE_URL = ""

    val api: LyricsApi = try {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LyricsApi::class.java)
    } catch (e: Exception) {
        Log.e("ApiClient", "Retrofit init failed", e)
        throw RuntimeException("Failed to initialize Retrofit")
    }
}

// --- ViewModel ---
class LyricsViewModel : ViewModel() {
    var uiState by mutableStateOf<LyricsUiState>(LyricsUiState.Idle)
        private set

    fun searchLyrics(song: String) {
        viewModelScope.launch  {
            uiState = LyricsUiState.Loading
            try {
                Log.e("ApiClient", "hithit")

                val response = ApiClient.api.getLyrics(song)
                //val response = LyricsResponse("日本語の歌詞", "Nihongo no kashi", "Japanese lyrics in English")
                uiState = LyricsUiState.Success(response)
            } catch (e: Exception) {
                Log.e("ApiClient", "Exception in API call: ${e.message}", e)
                uiState = LyricsUiState.Error("Lyrics not found.")
            }
        }
    }
}

sealed class LyricsUiState {
    object Idle : LyricsUiState()
    object Loading : LyricsUiState()
    data class Success(val data: LyricsResponse) : LyricsUiState()
    data class Error(val message: String) : LyricsUiState()
}

// --- Composable UI ---
@Composable
fun LyricsScreen(viewModel: LyricsViewModel = viewModel()) {
    var songName by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = songName,
            onValueChange = { songName = it },
            label = { Text("Enter song name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button (onClick = { viewModel.searchLyrics(songName) }) {
            Text("Search")
        }

        when (val state = viewModel.uiState) {
            is LyricsUiState.Loading -> Text("Loading...")
            is LyricsUiState.Success -> LyricsResult(state.data)
            is LyricsUiState.Error -> Text(state.message, color = Color.Red)
            LyricsUiState.Idle -> {}
        }
    }
}

@Composable
fun LyricsResult(data: LyricsResponse) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        Text("song name:")
        Text(data.songName)

        Text("url:")
        Text(data.url)

        Text("Lyric:")
        Text(data.lyric)
    }
}

