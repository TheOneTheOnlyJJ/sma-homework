package com.jurjandreigeorge.defroster.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heating_stats")
data class HeatingStatsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "start_time")
    val startTime: String,

    @ColumnInfo(name = "end_time")
    val endTime: String,

    @ColumnInfo(name = "start_temp")
    val startTemp: Float,

    @ColumnInfo(name = "target_temp")
    val targetTemp: Int,

    @ColumnInfo(name = "end_temp")
    val endTemp: Float,

    @ColumnInfo(name = "time_series_timestamps")
    val timeSeriesTimestamps: String,

    @ColumnInfo(name = "time_series_temps")
    val timeSeriesTemps: String
)
