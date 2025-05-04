package com.example.learnical.spotify

import android.app.Activity
import android.content.Intent

interface SpotifyAuthorizationService {

    /**
     * Ask the user for permission to access it's spotify player.
     * The user must have the spotify app installed on their device.
     * The method opens spotify activity
     * @param contextActivity the activity that is calling the method
     * @param clientId the clientId of the registered app in the spotify developer dashboard
     * @param redirectUri the redirectUri of the registered app in the spotify developer dashboard
     * @param requestCode the requestCode for opening new activity
     * */
    fun authorizeClient(contextActivity : Activity, clientId: String = SpotifyConstants.spotifyClientId, redirectUri: String = SpotifyConstants.redirectUri, requestCode : Int)
    

    /**
     * validate the authorization attempt of the user
     * @param resultCode the result code of the activity
     * @param data the intent of the activity
     * @return true if the user authorized the app, false otherwise
     * */
    fun validateAuthorization(resultCode: Int, data: Intent?) : Boolean
}