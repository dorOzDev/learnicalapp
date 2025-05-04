package com.example.learnical.spotify

import android.app.Activity
import android.content.Intent
import com.example.learnical.BuildConfig
import com.spotify.android.appremote.api.SpotifyAppRemote

interface SpotifyAuthorizationService {

    val spotifyClientId: String
        get() = BuildConfig.SPOTIFY_CLIENT_ID

    val spotifyRedirectUri: String
        get() = "${BuildConfig.BACK_END_URL}${BuildConfig.SPOTIFY_CALLBACK}"


    /**
     * Ask the user for permission to access it's spotify player.
     * The user must have the spotify app installed on their device.
     * The method opens spotify activity
     * @param contextActivity the activity that is calling the method
     * @param clientId the clientId of the registered app in the spotify developer dashboard
     * @param redirectUri the redirectUri of the registered app in the spotify developer dashboard
     * @param requestCode the requestCode for opening new activity
     * */
    fun authorizeClient(contextActivity : Activity, clientId: String = spotifyClientId, redirectUri: String = spotifyRedirectUri, requestCode : Int)
    

    /**
     * validate the authorization attempt of the user
     * @param resultCode the result code of the activity
     * @param data the intent of the activity
     * @return true if the user authorized the app, false otherwise
     * */
    fun validateAuthorization(resultCode: Int, data: Intent?) : Boolean
}