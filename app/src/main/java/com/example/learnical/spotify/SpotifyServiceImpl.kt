package com.example.learnical.spotify

import android.app.Activity
import android.content.Intent
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import jakarta.inject.Inject

class SpotifyServiceImpl @Inject constructor() : SpotifyService {

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

    override fun onActivityResult(resultCode: Int, data: Intent?) {
        TODO("Not yet implemented")
    }
}