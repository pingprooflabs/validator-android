package com.example.ping_proof.PingServices

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface PingService {
    @GET
    suspend fun pingUrl(@Url fullUrl: String): Response<Void>
}