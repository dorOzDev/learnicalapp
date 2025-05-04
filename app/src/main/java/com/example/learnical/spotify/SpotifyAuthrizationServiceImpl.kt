package com.example.learnical.spotify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import jakarta.inject.Inject

class SpotifyAuthrizationServiceImpl @Inject constructor() : SpotifyAuthorizationService {

    private var spotifyAppRemote: SpotifyAppRemote? = null

    override fun authorizeClient(
        contextActivity: Activity,
        clientId: String,
        redirectUri: String,
        requestCode: Int
    ) {
        var builder = AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.CODE, redirectUri);
        builder.setScopes(arrayOf("streaming"))
        var request = builder.build()

        AuthorizationClient.openLoginActivity(contextActivity, requestCode, request)
    }

    override fun validateAuthorization(
        resultCode: Int,
        intent: Intent?
    ): Boolean {
        var response = AuthorizationClient.getResponse(resultCode, intent)
        return when(response.type) {
            AuthorizationResponse.Type.CODE -> true
            AuthorizationResponse.Type.TOKEN -> {
                Log.e("SpotifyService", "shouldn't get here, authorization is expected to be of type code")
                false
            }
            else -> {
                Log.d("SpotifyService", "failed with response: $response")
                false
            }
        }
    }

    private fun connectSpotifyRemote(context: Context) {
        val connectionParams = ConnectionParams.Builder(spotifyClientId)
            .setRedirectUri(spotifyRedirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
            }
        })
    }
}