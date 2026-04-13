package com.jionifamily.domain.model

data class WeeklyStats(
    val weekStart: String,
    val totalMissions: Int,
    val completedMissions: Int,
    val approvedMissions: Int,
    val rejectedMissions: Int,
    val coinsEarned: Int,
)
