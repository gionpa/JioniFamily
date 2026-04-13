package com.jionifamily.domain.repository

import com.jionifamily.domain.model.Mission
import com.jionifamily.domain.model.MissionCompletion
import com.jionifamily.domain.model.WeeklyStats
import com.jionifamily.util.Result

interface MissionRepository {
    suspend fun getMissions(week: String? = null): Result<List<Mission>>
    suspend fun createMission(
        name: String,
        description: String?,
        rewardCoins: Int,
        category: String,
        isRecurring: Boolean,
    ): Result<Mission>
    suspend fun getCompletions(week: String? = null, status: String? = null): Result<List<MissionCompletion>>
    suspend fun submitMission(missionId: String): Result<MissionCompletion>
    suspend fun approveCompletion(completionId: String, comment: String?): Result<MissionCompletion>
    suspend fun rejectCompletion(completionId: String, comment: String?): Result<MissionCompletion>
    suspend fun getWeeklyStats(week: String? = null): Result<WeeklyStats>
    suspend fun getHistory(limit: Int = 12): Result<List<WeeklyStats>>
}
