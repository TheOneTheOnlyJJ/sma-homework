package com.example.smaproject

import java.time.LocalDateTime
import java.time.Duration

data class HeatingStats(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val startTemp: Float,
    val targetTemp: Int,
    val endTemp: Float
) {
    val duration: Duration = Duration.between(this.startTime, this.endTime)
}