package com.example.ping_proof.PingServices

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PingClient {
    val pingClient: PingService = Retrofit.Builder()
        .baseUrl("http://localhost/") // This will be overridden by @Url
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(PingService::class.java)

    suspend fun pingUrl(url: String): Response<Void> {
        return pingClient.pingUrl(url)
    }

}