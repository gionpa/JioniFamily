package com.jionifamily.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dayNames = arrayOf("월", "화", "수", "목", "금", "토", "일")

/**
 * Format week range from weekStart (Sunday) ISO date string.
 * e.g. "2026-04-12" → "4/12 (일) ~ 4/18 (토)"
 */
fun formatWeekRange(weekStart: String): String {
    if (weekStart.isBlank()) return ""
    return try {
        val start = LocalDate.parse(weekStart, DateTimeFormatter.ISO_LOCAL_DATE)
        val end = start.plusDays(6)
        val startDay = dayNames[start.dayOfWeek.value - 1]
        val endDay = dayNames[end.dayOfWeek.value - 1]
        "${start.monthValue}/${start.dayOfMonth} ($startDay) ~ ${end.monthValue}/${end.dayOfMonth} ($endDay)"
    } catch (e: Exception) {
        weekStart
    }
}
