package com.example.learnical.lyrics

import com.example.learnical.lyrics.network.LyricsResponse

interface LyricsRepository {

    suspend fun getLyrics(song: String): LyricsResponse
}