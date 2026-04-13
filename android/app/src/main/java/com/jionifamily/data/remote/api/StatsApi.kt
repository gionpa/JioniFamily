package com.jionifamily.data.remote.api

import com.jionifamily.data.remote.dto.HistoryResponse
import com.jionifamily.data.remote.dto.WeeklyStatsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StatsApi {
    @GET("stats/weekly")
    suspend fun getWeeklyStats(@Query("week") week: String? = null): Response<WeeklyStatsResponse>

    @GET("stats/history")
    suspend fun getHistory(@Query("limit") limit: Int = 12): Response<HistoryResponse>
}
