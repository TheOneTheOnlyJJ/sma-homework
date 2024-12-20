package com.jurjandreigeorge.defroster.domain

import android.util.Log
import com.jurjandreigeorge.defroster.data.HeatingStats
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

class HeatingStatsTracker {
    private var startTime: Date? = null
    private var endTime: Date? = null
    private var startTemp: Float? = null
    private var targetTemp: Int? = null
    private var endTemp: Float? = null
    private val formatter = SimpleDateFormat(dateTimePattern, java.util.Locale.US)

    private fun hasStartedTracking(): Boolean {
        return this.startTime != null && this.startTemp != null && this.targetTemp != null
    }

    private fun hasFinishedTracking(): Boolean {
        return this.hasStartedTracking() && this.endTime != null && this.endTemp != null
    }

    fun startTracking(startTemp: Float, targetTemp: Int) {
        Log.i("Heating Tracker", "Starting tracking heating.")
        this.startTime = Date.from(Instant.now())
        this.startTemp = startTemp
        this.targetTemp = targetTemp
    }

    fun stopTracking(endTemp: Float): HeatingStats {
        if (!this.hasStartedTracking()) {
            throw RuntimeException("Cannot stop tracking while tracking not started!")
        }
        Log.i("Heating Tracker", "Stopping tracking heating.")
        this.endTime = Date.from(Instant.now())
        this.endTemp = endTemp
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
    }

    private fun getStatsMap(): HeatingStats {
        if (!this.hasFinishedTracking()) {
            throw RuntimeException("Cannot get stats map while tracking not finished!")
        }
        return HeatingStats(
            startTime = this.formatter.format(this.startTime!!),
            endTime = this.formatter.format(this.endTime!!),
            startTemp = this.startTemp!!,
            targetTemp = this.targetTemp!!,
            endTemp = this.endTemp!!
        )
    }
}
