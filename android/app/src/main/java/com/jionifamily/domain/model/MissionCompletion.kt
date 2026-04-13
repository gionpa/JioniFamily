package com.jionifamily.domain.model

data class MissionCompletion(
    val id: String,
    val missionId: String,
    val missionName: String?,
    val missionRewardCoins: Int?,
    val missionCategory: MissionCategory?,
    val childId: String,
    val weekStart: String,
    val status: CompletionStatus,
    val submittedAt: String?,
    val reviewedAt: String?,
    val reviewerComment: String?,
)

enum class CompletionStatus(val label: String) {
    PENDING("대기중"),
    SUBMITTED("확인중"),
    APPROVED("승인됨"),
    REJECTED("거절됨"),
    MISSED("미완료");

    companion object {
        fun fromString(value: String): CompletionStatus = when (value) {
            "submitted" -> SUBMITTED
            "approved" -> APPROVED
            "rejected" -> REJECTED
            "missed" -> MISSED
            else -> PENDING
        }
    }
}
