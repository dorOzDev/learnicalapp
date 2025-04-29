package com.example.learnical.spotify

import android.app.Activity
import android.content.Intent

interface SpotifyService {

    /**
     * Ask the user for permission to access it's spotify player.
     * The user must have the spotify app installed on their device.
     * The method opens spotify activity
     * @param contextActivity the activity that is calling the method
     * @param clientId the clientId of the registered app in the spotify developer dashboard
     * @param redirectUri the redirectUri of the registered app in the spotify developer dashboard
     * @param requestCode the requestCode for opening new activity
     * */
    fun authorizeClient(contextActivity : Activity, clientId: String, redirectUri: String, requestCode : Int)

    /**
     * handle authorize to spotify client attempt.
     * If succeed will attach a listener to the current spotify client
     * */
    fun onActivityResult(resultCode: Int, data: Intent?)

    /**
     * disconnet from spotify remote client
     * */
    fun disconnetSpotify()
}