package com.jionifamily.data.repository

import com.jionifamily.data.remote.api.CompletionApi
import com.jionifamily.data.remote.api.MissionApi
import com.jionifamily.data.remote.api.StatsApi
import com.jionifamily.data.remote.dto.MissionCreateRequest
import com.jionifamily.data.remote.dto.ReviewRequest
import com.jionifamily.domain.model.CompletionStatus
import com.jionifamily.domain.model.Mission
import com.jionifamily.domain.model.MissionCategory
import com.jionifamily.domain.model.MissionCompletion
import com.jionifamily.domain.model.WeeklyStats
import com.jionifamily.domain.repository.MissionRepository
import com.jionifamily.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MissionRepositoryImpl @Inject constructor(
    private val missionApi: MissionApi,
    private val completionApi: CompletionApi,
    private val statsApi: StatsApi,
) : MissionRepository {

    override suspend fun getMissions(week: String?): Result<List<Mission>> = apiCall {
        missionApi.getMissions(week).body()!!.map { dto ->
            Mission(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                rewardCoins = dto.rewardCoins,
                category = MissionCategory.fromString(dto.category),
                isRecurring = dto.isRecurring,
                isActive = dto.isActive,
                createdBy = dto.createdBy,
                creatorName = dto.creatorName,
            )
        }
    }

    override suspend fun createMission(
        name: String,
        description: String?,
        rewardCoins: Int,
        category: String,
        isRecurring: Boolean,
    ): Result<Mission> = apiCall {
        val dto = missionApi.createMission(
            MissionCreateRequest(name, description, rewardCoins, category, isRecurring)
        ).body()!!
        Mission(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            rewardCoins = dto.rewardCoins,
            category = MissionCategory.fromString(dto.category),
            isRecurring = dto.isRecurring,
            isActive = dto.isActive,
            createdBy = dto.createdBy,
            creatorName = dto.creatorName,
        )
    }

    override suspend fun getCompletions(week: String?, status: String?): Result<List<MissionCompletion>> = apiCall {
        completionApi.getCompletions(week, status).body()!!.map { it.toDomain() }
    }

    override suspend fun submitMission(missionId: String): Result<MissionCompletion> = apiCall {
        completionApi.submitMission(missionId).body()!!.toDomain()
    }

    override suspend fun approveCompletion(completionId: String, comment: String?): Result<MissionCompletion> = apiCall {
        completionApi.approve(completionId, ReviewRequest(comment)).body()!!.toDomain()
    }

    override suspend fun rejectCompletion(completionId: String, comment: String?): Result<MissionCompletion> = apiCall {
        completionApi.reject(completionId, ReviewRequest(comment)).body()!!.toDomain()
    }

    override suspend fun getWeeklyStats(week: String?): Result<WeeklyStats> = apiCall {
        val dto = statsApi.getWeeklyStats(week).body()!!
        WeeklyStats(
            weekStart = dto.weekStart,
            totalMissions = dto.totalMissions,
            completedMissions = dto.completedMissions,
            approvedMissions = dto.approvedMissions,
            rejectedMissions = dto.rejectedMissions,
            coinsEarned = dto.coinsEarned,
        )
    }

    override suspend fun getHistory(limit: Int): Result<List<WeeklyStats>> = apiCall {
        statsApi.getHistory(limit).body()!!.weeks.map { dto ->
            WeeklyStats(
                weekStart = dto.weekStart,
                totalMissions = dto.totalMissions,
                completedMissions = dto.completedMissions,
                approvedMissions = dto.approvedMissions,
                rejectedMissions = dto.rejectedMissions,
                coinsEarned = dto.coinsEarned,
            )
        }
    }

    private fun com.jionifamily.data.remote.dto.CompletionResponse.toDomain() = MissionCompletion(
        id = id,
        missionId = missionId,
        missionName = missionName,
        missionRewardCoins = missionRewardCoins,
        missionCategory = missionCategory?.let { MissionCategory.fromString(it) },
        childId = childId,
        weekStart = weekStart,
        status = CompletionStatus.fromString(status),
        submittedAt = submittedAt,
        reviewedAt = reviewedAt,
        reviewerComment = reviewerComment,
    )

    private suspend fun <T> apiCall(block: suspend () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: Exception) {
            Result.Error("네트워크 오류가 발생했어요")
        }
    }
}
