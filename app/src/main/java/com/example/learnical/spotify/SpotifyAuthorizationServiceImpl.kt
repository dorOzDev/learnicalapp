package com.example.learnical.spotify

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import jakarta.inject.Inject

class SpotifyAuthorizationServiceImpl @Inject constructor() : SpotifyAuthorizationService {

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
}