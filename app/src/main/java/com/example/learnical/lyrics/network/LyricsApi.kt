package com.example.learnical.lyrics.network

import retrofit2.http.GET
import retrofit2.http.Query

interface LyricsApi {

    @GET("search?")
    suspend fun getLyrics(@Query("song_name") songName: String): LyricsResponse
}