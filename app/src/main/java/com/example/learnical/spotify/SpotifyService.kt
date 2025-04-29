package com.example.learnical.spotify

interface SpotifyService {

    /**
     * Ask the user for permission to access it's spotify player.
     * The user must have the spotify app installed on their device.
     * @param clientId the clientId of the registered app in the spotify developer dashboard
     * @param redirectUri the redirectUri of the registered app in the spotify developer dashboard
     * */
    fun authorizeClient(clientId: String, redirectUri: String)
}