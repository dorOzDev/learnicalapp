package com.example.learnical.lyrics

import com.example.learnical.lyrics.network.ApiClient
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject

@ViewModelScoped
class LyricsRepositoryImpl @Inject constructor() : LyricsRepository {

    override suspend fun getLyrics(song: String) = ApiClient.api.getLyrics(song)
}