package com.example.learnical.sever

interface ServerService {

    suspend fun isServerRunning(): Boolean
}