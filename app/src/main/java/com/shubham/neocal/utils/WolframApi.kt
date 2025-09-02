package com.shubham.neocal.utils

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response


interface WolframApi {
    @GET("v2/query")
    suspend fun getSteps(
        @Query("appid") appId: String,
        @Query("input") input: String,
        @Query("format") format: String = "plaintext",
        @Query("podstate") podstate: String? = null
    ): WolframResponse
}