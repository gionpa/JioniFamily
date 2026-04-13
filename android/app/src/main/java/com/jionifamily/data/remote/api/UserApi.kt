package com.jionifamily.data.remote.api

import com.jionifamily.data.remote.dto.UserResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("users/me")
    suspend fun getMe(): Response<UserResponse>
}
