package com.example.area.api

import com.example.area.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header

interface SimpleApi {
    @POST("users")
    suspend fun register(@Body post: RegisterFields): Response<Token>

    @POST("users/login")
    suspend fun login(@Body post: LoginFields): Response<Token>

    @GET("users/areas")
    suspend fun getUserAreaList(@Header("Authorization") auth: String): Response<List<ActionReaction>>

    @POST("areas")
    suspend fun createArea(@Body post: AREAFields): Response<Token>
}