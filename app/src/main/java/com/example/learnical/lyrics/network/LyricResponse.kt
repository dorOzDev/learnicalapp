package com.example.learnical.lyrics.network

data class LyricsResponse(
    val songName: String,
    val url: String,
    val lyric: String
)