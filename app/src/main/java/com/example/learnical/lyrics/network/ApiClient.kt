package com.example.learnical.lyrics.network

import android.util.Log
import com.example.learnical.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    private const val BASE_URL = "${BuildConfig.BACK_END_URL}jap/"

    val api: LyricsApi = try {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LyricsApi::class.java)
    } catch (e: Exception) {
        Log.e("ApiClient", "Retrofit init failed", e)
        throw RuntimeException("Failed to initialize Retrofit")
    }
}