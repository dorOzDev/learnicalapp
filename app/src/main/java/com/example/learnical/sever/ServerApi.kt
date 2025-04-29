package com.example.learnical.sever

import retrofit2.Response
import retrofit2.http.GET

interface ServerApi {

    @GET("sanity")
    suspend fun isServerUp() : Response<Void>
}