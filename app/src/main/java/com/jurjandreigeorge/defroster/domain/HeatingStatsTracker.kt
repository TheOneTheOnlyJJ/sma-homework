package com.jurjandreigeorge.defroster.domain

import android.util.Log
import com.jurjandreigeorge.defroster.data.HeatingStatsEntity
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Timer
import kotlin.concurrent.timerTask

class HeatingStatsTracker(
    private val getCurrentTemp: () -> Float
) {
    private var startTime: Date? = null
    private var endTime: Date? = null
    private var startTemp: Float? = null
    private var targetTemp: Int? = null
    private var endTemp: Float? = null
    private var timeSeries: MutableMap<String, Float>? = null
    private val formatter = SimpleDateFormat(dateTimePattern, java.util.Locale.US)
    // Time series manipulation
    private var addToTimeSeriesTimer: Timer? = null
    private val addToTimeSeriesPeriod: Long = 5_000L

    private fun hasStartedTracking(): Boolean {
        return this.startTime != null
                && this.startTemp != null
                && this.targetTemp != null
                && this.timeSeries != null
    }

    private fun hasStoppedTracking(): Boolean {
        return this.hasStartedTracking()
                && this.endTime != null
                && this.endTemp != null
                && this.timeSeries != null
    }

    private fun hasClearedHeatingStats(): Boolean {
        return this.startTime == null
                && this.endTime == null
                && this.startTemp == null
                && this.targetTemp == null
                && this.endTemp == null
                && this.timeSeries == null
    }

    fun startTracking(targetTemp: Int) {
        Log.i("Heating Tracker", "Starting tracking heating.")
        if (!this.hasClearedHeatingStats()) {
            throw RuntimeException("Cannot start tracking before clearing previous heating stats!")
        }
        this.startTime = Date.from(Instant.now())
        this.startTemp = this.getCurrentTemp()
        this.targetTemp = targetTemp
        this.timeSeries = mutableMapOf(this.formatter.format(this.startTime!!) to this.startTemp!!)
        this.addToTimeSeriesTimer = Timer()
        this.addToTimeSeriesTimer!!.schedule(
            timerTask {
                addToTimeSeries(Date.from(Instant.now()), getCurrentTemp())
            },
            this.addToTimeSeriesPeriod,
            this.addToTimeSeriesPeriod
        )
        Log.i("Heating Tracker", "Started tracking heating.")
    }

    fun stopTracking(): HeatingStatsEntity {
        Log.i("Heating Tracker", "Stopping tracking heating.")
        if (!this.hasStartedTracking()) {
            throw RuntimeException("Cannot stop tracking heating while tracking not started!")
        }
        // Clear time series timer
        this.addToTimeSeriesTimer!!.cancel()
        this.addToTimeSeriesTimer!!.purge()
        this.addToTimeSeriesTimer = null
        // Add end time to series
        this.endTime = Date.from(Instant.now())
        this.endTemp = this.getCurrentTemp()
        this.timeSeries!![this.formatter.format(this.endTime!!)] = this.endTemp!!
        Log.i("Heating Tracker", "Stopped tracking heating.")
        val heatingStats =  this.getStatsMap()
        this.clearTrackingStats()
        return heatingStats
    }

    private fun clearTrackingStats() {
        Log.i("Heating Tracker", "Clearing heating tracking stats.")
        this.startTime = null
        this.endTime = null
        this.startTemp = null
        this.targetTemp = null
        this.endTemp = null
        this.timeSeries = null
    }

    private fun addToTimeSeries(time: Date, temp: Float) {
        Log.i("Heating Tracker", "Adding to time series: $time, $temp.")
        if (!this.hasStartedTracking()) {
            throw RuntimeException("Cannot add to time series while tracking not started!")
        }
        this.timeSeries!![this.formatter.format(time)] = temp
    }

    private fun getStatsMap(): HeatingStatsEntity {
        if (!this.hasStoppedTracking()) {
            throw RuntimeException("Cannot get stats map while tracking not finished!")
        }
        return HeatingStatsEntity(
            startTime = this.formatter.format(this.startTime!!),
            endTime = this.formatter.format(this.endTime!!),
            startTemp = this.startTemp!!,
            targetTemp = this.targetTemp!!,
            endTemp = this.endTemp!!,
            timeSeriesTimestamps = this.timeSeries!!.map { it.key }.toString(),
            timeSeriesTemps = this.timeSeries!!.map { it.value }.toString()
        )
    }
}
