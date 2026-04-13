package com.jionifamily.data.remote.api

import com.jionifamily.data.remote.dto.CompletionResponse
import com.jionifamily.data.remote.dto.ReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CompletionApi {
    @GET("missions/completions")
    suspend fun getCompletions(
        @Query("week") week: String? = null,
        @Query("status") status: String? = null,
    ): Response<List<CompletionResponse>>

    @POST("missions/{missionId}/submit")
    suspend fun submitMission(@Path("missionId") missionId: String): Response<CompletionResponse>

    @POST("completions/{completionId}/approve")
    suspend fun approve(
        @Path("completionId") completionId: String,
        @Body request: ReviewRequest,
    ): Response<CompletionResponse>

    @POST("completions/{completionId}/reject")
    suspend fun reject(
        @Path("completionId") completionId: String,
        @Body request: ReviewRequest,
    ): Response<CompletionResponse>
}
