package com.example.learnical.sever

import com.example.learnical.network.ApiClient
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject


@ViewModelScoped
class ServerServiceImpl @Inject constructor() : ServerService {

    override suspend fun isServerRunning(): Boolean {
        val serverUp = ApiClient.serverApi.isServerUp()
        return serverUp.isSuccessful
    }
}