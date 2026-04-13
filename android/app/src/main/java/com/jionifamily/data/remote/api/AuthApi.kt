package com.jionifamily.data.remote.api

import com.jionifamily.data.remote.dto.ChangePinRequest
import com.jionifamily.data.remote.dto.LoginRequest
import com.jionifamily.data.remote.dto.RefreshRequest
import com.jionifamily.data.remote.dto.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<TokenResponse>

    @PUT("auth/pin")
    suspend fun changePin(@Body request: ChangePinRequest): Response<Unit>
}
