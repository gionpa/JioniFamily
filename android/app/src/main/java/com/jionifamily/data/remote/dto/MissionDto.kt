package com.jionifamily.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MissionResponse(
    val id: String,
    val name: String,
    val description: String?,
    @SerializedName("reward_coins") val rewardCoins: Int,
    val category: String,
    @SerializedName("is_recurring") val isRecurring: Boolean,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("creator_name") val creatorName: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)

data class MissionCreateRequest(
    val name: String,
    val description: String?,
    @SerializedName("reward_coins") val rewardCoins: Int,
    val category: String,
    @SerializedName("is_recurring") val isRecurring: Boolean,
)

data class CompletionResponse(
    val id: String,
    @SerializedName("mission_id") val missionId: String,
    @SerializedName("mission_name") val missionName: String?,
    @SerializedName("mission_reward_coins") val missionRewardCoins: Int?,
    @SerializedName("mission_category") val missionCategory: String?,
    @SerializedName("child_id") val childId: String,
    @SerializedName("week_start") val weekStart: String,
    val status: String,
    @SerializedName("submitted_at") val submittedAt: String?,
    @SerializedName("reviewed_at") val reviewedAt: String?,
    @SerializedName("reviewed_by") val reviewedBy: String?,
    @SerializedName("reviewer_comment") val reviewerComment: String?,
)

data class ReviewRequest(
    val comment: String?,
)

data class WeeklyStatsResponse(
    @SerializedName("week_start") val weekStart: String,
    @SerializedName("total_missions") val totalMissions: Int,
    @SerializedName("completed_missions") val completedMissions: Int,
    @SerializedName("approved_missions") val approvedMissions: Int,
    @SerializedName("rejected_missions") val rejectedMissions: Int,
    @SerializedName("coins_earned") val coinsEarned: Int,
)

data class HistoryResponse(
    val weeks: List<WeeklyStatsResponse>,
)
