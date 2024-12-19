package com.example.defroster.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.defroster.data.HeatingStats
import com.example.defroster.data.HeatingStatsDao
import com.example.defroster.domain.HeatingState
import com.example.defroster.domain.HeatingStatsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class DefrosterViewModel @Inject constructor(
    private val heatingStatsDao: HeatingStatsDao
) : ViewModel() {
    var currentTemp by mutableFloatStateOf(0f)
    var targetTemp by mutableIntStateOf(10)
    private val targetTempTolerance = 2
    val targetTempLowerLimit by derivedStateOf { this.targetTemp - this.targetTempTolerance }
    val targetTempUpperLimit by derivedStateOf { this.targetTemp + this.targetTempTolerance }
    var heatingState by mutableStateOf(HeatingState.NOT_HEATING)
        private set
    private val heatingThreadIterationCycleLoopCount = 100_000_000
    private val heatingThreadSleepTime = 5_000L
    private val heatingThreads = mutableStateListOf<Thread>()
    private val heatingStatsTracker: HeatingStatsTracker = HeatingStatsTracker()
    val heatingStatsFlow: Flow<List<HeatingStats>> = heatingStatsDao.loadAll()

    var isHeatingCardListReversed by mutableStateOf(false)

    fun toggleHeating() {
        when (this.heatingState) {
            HeatingState.NOT_HEATING -> {
                this.startHeating()
            }
            HeatingState.HEATING -> {
                this.stopHeating()
            }
            else -> {
                throw RuntimeException("Should not toggle heating while stopping heating!")
            }
        }
    }

    private fun runHeatingCycle() {
        var meaninglessCounter = 0
        for (j in 0..this.heatingThreadIterationCycleLoopCount) {
            meaninglessCounter += 1
        }
    }

    private fun heatingThreadImplementation(logTag: String) {
        var heatingCycle = BigInteger.ZERO
        var doRunHeating: Boolean
        var isInActiveHeatingMode = true
        Log.i(logTag, "Starting.")
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted) {
                    throw InterruptedException()
                }
                if (isInActiveHeatingMode) {
                    if (this.currentTemp >= this.targetTempUpperLimit) {
                        // Exit active heating mode when temperature reaches or exceeds upper limit
                        doRunHeating = false
                        isInActiveHeatingMode = false
                        Log.i(logTag, "Current temp. reached upper limit. Exiting active heating mode.")
                    } else {
                        // Continue heating until upper limit is reached
                        doRunHeating = true
                    }
                } else {
                    if (this.currentTemp < this.targetTempLowerLimit) {
                        // Re-enter active heating mode when temperature drops below lower limit
                        doRunHeating = true
                        isInActiveHeatingMode = true
                        Log.i(logTag, "Current temp. below lower limit. Re-entering active heating mode.")
                    } else {
                        // Maintain sleep state while within tolerance range
                        doRunHeating = false
                    }
                }
                if (doRunHeating) {
                    this.runHeatingCycle()
                    heatingCycle = heatingCycle.add(BigInteger.ONE)
                    Log.i(logTag, "Completed heating cycle no. $heatingCycle.")
                } else {
                    Log.i(logTag, "Going to sleep ${this.heatingThreadSleepTime} ms.")
                    Thread.sleep(this.heatingThreadSleepTime)
                }
            }
        } catch (e: InterruptedException) {
            Log.i(logTag, "Interrupted. Stopping.")
        }
    }

    private fun startHeating() {
        Log.i(
            "Defroster",
            "Starting heating with target temperature of ${this.targetTemp} °C."
        )
        this.heatingState = HeatingState.HEATING
        this.heatingStatsTracker.startTracking(this.currentTemp, this.targetTemp)
        val availableProcessors = Runtime.getRuntime().availableProcessors()
        Log.i("Defroster", "Device has $availableProcessors available processors.")
        for (i in 1..availableProcessors) {
            val newHeatingThread = Thread {
                this.heatingThreadImplementation("Defroster Heating Thread No. $i")
            }
            this.heatingThreads.add(newHeatingThread)
        }
        for (i in 0..<this.heatingThreads.size) {
            this.heatingThreads[i].start()
        }
    }

    private fun stopHeating() {
        Log.i("Defroster", "Stopping heating.")
        this.heatingState = HeatingState.STOPPING_HEATING
        for (thread in this.heatingThreads) {
            thread.interrupt()
        }
        for (thread in this.heatingThreads) {
            thread.join()
        }
        this.heatingThreads.clear()
        Log.i("Defroster", "All heating threads stopped.")
        val heatingStats = this.heatingStatsTracker.stopTracking(this.currentTemp)
        Log.i("Defroster", "Heating stats: ${heatingStats}.")
        Log.i("Defroster", "Launching database insertion coroutine.")
        CoroutineScope(Dispatchers.IO).launch {
            Log.i(
                "Defroster Database Insertion Coroutine",
                "Inserting heating stats.")
            val insertedId = heatingStatsDao.insert(heatingStats)
            Log.i(
                "Defroster Database Insertion Coroutine",
                "Inserted heating stats. Inserted ID: $insertedId."
            )
        }
        this.heatingState = HeatingState.NOT_HEATING
    }

    fun deleteHeatingStats(vararg heatingStats: HeatingStats) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i(
                "Defroster Database Deletion Coroutine",
                "Deleting heating stats with IDs: ${heatingStats.map { it.id }}."
            )
            val deletedId = heatingStatsDao.delete(*heatingStats)
            Log.i(
                "Defroster Database Deletion Coroutine",
                "Deleted heating stats. Entries deleted: $deletedId."
            )
        }
    }
}
