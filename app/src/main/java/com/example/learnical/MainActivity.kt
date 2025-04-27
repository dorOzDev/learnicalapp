package com.example.learnical

import android.app.ComponentCaller
import android.content.Intent
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
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
// --- Entry Point ---
class MainActivity : ComponentActivity() {

    private val baseBackendUrl = ""

    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    private val redirectUri = "https://0dda-176-230-145-233.ngrok-free.app/api/callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val requestCode = 1337

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LyricsScreen(mainActivity = this)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        connectSpotifyRemote()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller) {
        super.onActivityResult(requestCode, resultCode, data, caller)

        if (requestCode == this.requestCode) {
            var response = AuthorizationClient.getResponse(resultCode, intent)

            when(response.type) {
                AuthorizationResponse.Type.CODE -> connectSpotifyRemote()
                AuthorizationResponse.Type.TOKEN -> connectSpotifyRemote()
                AuthorizationResponse.Type.ERROR -> {}
                AuthorizationResponse.Type.EMPTY -> {}
                AuthorizationResponse.Type.UNKNOWN -> {}
            }
        }
    }

    private fun connectSpotifyRemote() {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }

    fun authSpotify() {
        var builder = AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.CODE, redirectUri);
        builder.setScopes(arrayOf("streaming"))
        var request = builder.build()

        AuthorizationClient.openLoginActivity(this, requestCode, request)
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)

        val uri = intent.data
        val res = AuthorizationResponse.fromUri(uri)

        when(res.type) {
            AuthorizationResponse.Type.CODE -> connectSpotifyRemote()
            AuthorizationResponse.Type.TOKEN -> TODO()
            AuthorizationResponse.Type.ERROR -> TODO()
            AuthorizationResponse.Type.EMPTY -> TODO()
            AuthorizationResponse.Type.UNKNOWN -> TODO()
        }
        val code = uri?.getQueryParameter("code")
        val error = uri?.getQueryParameter("error")
    }

    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                val name = track.name

            }
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
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
fun LyricsScreen(viewModel: LyricsViewModel = viewModel(), mainActivity: MainActivity) {
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

        Button(onClick = { mainActivity.authSpotify() }) {
            Text("Auth Spotify")
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

