package com.example.smaproject

import android.util.Log
import java.time.LocalDateTime

class HeatingStatsTracker {
    private var startTime: LocalDateTime? = null
    private var endTime: LocalDateTime? = null
    private var startTemp: Float? = null
    private var targetTemp: Int? = null
    private var endTemp: Float? = null

    private fun hasStartedTracking(): Boolean {
        return this.startTime != null && this.startTemp != null && this.targetTemp != null
    }

    private fun hasFinishedTracking(): Boolean {
        return this.hasStartedTracking() && this.endTime != null && this.endTemp != null
    }

    fun startTracking(startTemp: Float, targetTemp: Int) {
        Log.i("Heating Tracker", "Starting tracking heating.")
        this.startTime = LocalDateTime.now()
        this.startTemp = startTemp
        this.targetTemp = targetTemp
    }

    fun stopTracking(endTemp: Float): HeatingStats {
        if (!this.hasStartedTracking()) {
            throw RuntimeException("Cannot stop tracking while tracking not started!")
        }
        Log.i("Heating Tracker", "Stopping tracking heating.")
        this.endTime = LocalDateTime.now()
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
        return HeatingStats(this.startTime!!, this.endTime!!, this.startTemp!!, this.targetTemp!!, this.endTemp!!)
    }
}
