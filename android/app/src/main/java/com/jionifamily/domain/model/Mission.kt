package com.jionifamily.domain.model

data class Mission(
    val id: String,
    val name: String,
    val description: String?,
    val rewardCoins: Int,
    val category: MissionCategory,
    val isRecurring: Boolean,
    val isActive: Boolean,
    val createdBy: String,
    val creatorName: String?,
)

enum class MissionCategory(val label: String, val emoji: String) {
    STUDY("공부", "\uD83D\uDCDA"),
    EXERCISE("운동", "\uD83C\uDFC3"),
    CHORES("집안일", "\uD83E\uDDF9"),
    OTHER("기타", "\uD83C\uDFAF");

    companion object {
        fun fromString(value: String): MissionCategory = when (value) {
            "study" -> STUDY
            "exercise" -> EXERCISE
            "chores" -> CHORES
            else -> OTHER
        }
    }
}
