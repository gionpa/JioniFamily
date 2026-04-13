package com.jionifamily.data.remote.api

import com.jionifamily.data.remote.dto.MissionCreateRequest
import com.jionifamily.data.remote.dto.MissionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MissionApi {
    @GET("missions")
    suspend fun getMissions(@Query("week") week: String? = null): Response<List<MissionResponse>>

    @POST("missions")
    suspend fun createMission(@Body request: MissionCreateRequest): Response<MissionResponse>
}
