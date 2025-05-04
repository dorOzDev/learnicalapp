package com.example.learnical

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.learnical.lyrics.ui.LyricsScreen
import kotlinx.coroutines.launch
import com.example.learnical.lyrics.presentation.LyricsViewModel
import com.example.learnical.sever.ServerViewModel
import com.example.learnical.spotify.SpotifyAuthorizationService
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val requestCode = 1337
    private val lyricsViewModel: LyricsViewModel by viewModels()
    private val serverViewModel : ServerViewModel by viewModels()
    val spotifyClientId: String
        get() = BuildConfig.SPOTIFY_CLIENT_ID

    val redirectUri: String
        get() = "${BuildConfig.BACK_END_URL}${BuildConfig.SPOTIFY_CALLBACK}"

    @Inject
    lateinit var spotifyAuthorizationService: SpotifyAuthorizationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LyricsScreen(lyricsViewModel, serverViewModel) {
                    authorizeSpotify()
                }
            }
        }
        checkForServerStatus(60000L)
    }

    private fun authorizeSpotify() {
        spotifyAuthorizationService.authorizeClient(this, requestCode = requestCode)
        connectSpotifyRemote()
    }

    private fun checkForServerStatus(millis: Long) {
        serverViewModel.viewModelScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while(isActive) {
                    serverViewModel.fetchServerStatus()
                    serverViewModel.serverStatus
                    delay(millis)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        authorizeSpotify()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller) {
        super.onActivityResult(requestCode, resultCode, data, caller)

        if (requestCode == this.requestCode) {
            val validateAuthorization = spotifyAuthorizationService.validateAuthorization(requestCode, data)
            if(validateAuthorization) {
                connectSpotifyRemote()
            }
        }
    }

    private fun connectSpotifyRemote() {
        val connectionParams = ConnectionParams.Builder(spotifyClientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                subscribeToTrackPlayedCallback()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
            }
        })
    }

    private fun subscribeToTrackPlayedCallback() {
        spotifyAppRemote?.let {
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