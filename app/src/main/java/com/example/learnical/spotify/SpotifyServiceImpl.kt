package com.example.learnical.spotify

import jakarta.inject.Inject

class SpotifyServiceImpl @Inject constructor() : SpotifyService {

    override fun authorizeClient(clientId: String, redirectUri: String) {

    }
}