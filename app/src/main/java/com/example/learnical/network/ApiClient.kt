package com.example.learnical.network

import android.util.Log
import com.example.learnical.BuildConfig
import com.example.learnical.lyrics.network.LyricsApi
import com.example.learnical.sever.ServerApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = BuildConfig.BACK_END_URL

    val lyricApi: LyricsApi = try {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LyricsApi::class.java)
    } catch (e: Exception) {
        Log.e("ApiClient", "Retrofit init failed", e)
        throw RuntimeException("Failed to initialize Retrofit")
    }

    val serverApi : ServerApi = try {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServerApi::class.java)
    } catch (e: Exception) {
        Log.e("ApiClient", "Retrofit init failed", e)
        throw RuntimeException("Failed to initialize Retrofit")
    }
}