package com.example.learnical

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learnical.lyrics.ui.LyricsScreen
import kotlinx.coroutines.launch
import com.example.learnical.lyrics.presentation.LyricsViewModel
import com.example.learnical.spotify.SpotifyService
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    private val redirectUri = "${BuildConfig.BACK_END_URL}${BuildConfig.SPOTIFY_CALLBACK}"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val requestCode = 1337
    private val lyricsViewModel: LyricsViewModel by viewModels()

    @Inject
    lateinit var spotifyService: SpotifyService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LyricsScreen(lyricsViewModel) {
                    spotifyService.authorizeClient(this, clientId, redirectUri, requestCode)
                    connectSpotifyRemote()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
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