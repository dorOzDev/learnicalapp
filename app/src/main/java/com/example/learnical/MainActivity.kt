package com.example.learnical

import android.app.ComponentCaller
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.os.Bundle
import android.print.PrintAttributes
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {


    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    private val redirectUri = "${BuildConfig.BACK_END_URL}${BuildConfig.SPOTIFY_CALLBACK}"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val requestCode = 1337
    private lateinit var lyricsViewModel: LyricsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lyricsViewModel = LyricsViewModel()
        setContent {
            MaterialTheme {
                LyricsScreen(lyricsViewModel, this)
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
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
            }
        })
    }

    fun authSpotify() {
        var builder = AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.CODE, redirectUri);
        builder.setScopes(arrayOf("streaming"))
        var request = builder.build()

        AuthorizationClient.openLoginActivity(this, requestCode, request)
    }

    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                val trackName = track.name
                Log.d("MainActivity", "Playing song: $trackName")
                lifecycleScope.launch {
                    Log.d("MainActivity", "Getting lyrics for song: $trackName")
                    lyricsViewModel.onNewSong(trackName)

                }
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
    private const val BASE_URL = "${BuildConfig.BACK_END_URL}jap/"

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
    private val _uiState = MutableStateFlow<LyricsUiState>(LyricsUiState.Idle)
    val uiState: StateFlow<LyricsUiState> = _uiState.asStateFlow()

    //Change the state with a new song
    fun onNewSong(song: String) {
        viewModelScope.launch {
            _uiState.value = LyricsUiState.Loading
            try {
                val response = ApiClient.api.getLyrics(song)
                _uiState.value = LyricsUiState.Success(response)
            } catch (e: Exception) {
                Log.e("ApiClient", "Exception in API call: ${e.message}", e)
                _uiState.value = LyricsUiState.Error("Lyrics not found.")
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

@Composable
fun LyricsResult(data: LyricsResponse) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        Text("song name:")
        Text(data.songName)

        Text(
            buildAnnotatedString {
                append("Url ")
                withLink(
                    LinkAnnotation.Url(
                        data.url,
                        TextLinkStyles(style = SpanStyle(color = Color.Blue))
                    )
                ) {
                    append("song url")
                }
            }
        )

        Text("Lyric:")
        Text(data.lyric)
    }
}

