package com.jurjandreigeorge.defroster.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.jurjandreigeorge.defroster.data.HeatingStatsEntity
import com.jurjandreigeorge.defroster.data.room.HeatingStatsDao
import com.jurjandreigeorge.defroster.domain.HeatingState
import com.jurjandreigeorge.defroster.domain.HeatingStatsTracker
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
    private val heatingThreadSleepTime = 500L
    private val heatingThreads = mutableStateListOf<Thread>()

    private val heatingStatsTrackerCurrentTempSamplingPeriodSeconds = 1L
    private val heatingStatsTracker: HeatingStatsTracker = HeatingStatsTracker(
        getCurrentTemp = { this.currentTemp },
        currentTempSamplingPeriodSeconds = this.heatingStatsTrackerCurrentTempSamplingPeriodSeconds
    )

    val allHeatingStatsFlow: Flow<List<HeatingStatsEntity>> = heatingStatsDao.loadAllFlow()

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
        this.heatingStatsTracker.startTracking(this.targetTemp)
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
        val heatingStats = this.heatingStatsTracker.stopTracking()
        Log.i("Defroster", "Heating stats: ${heatingStats}.")
        this.heatingState = HeatingState.NOT_HEATING
        CoroutineScope(Dispatchers.IO).launch {
            insertHeatingStatsIntoRoom(heatingStats = listOf(heatingStats))
        }
    }

    private fun insertHeatingStatsIntoRoom(
        logTag: String = "Insert Heating Stats",
        heatingStats: List<HeatingStatsEntity>
    ): List<Long> {
        Log.i(logTag, "Inserting ${heatingStats.size} heating stats into Room Defroster Database.")
        if (heatingStats.isEmpty()) {
            Log.i(logTag, "No heating stats to insert.")
            return emptyList()
        }
        val insertedRowIds = heatingStatsDao.insertAll(heatingStats)
        Log.i(logTag, "Inserted heating stats. Inserted row IDs: $insertedRowIds.")
        return insertedRowIds
    }

    private fun deleteHeatingStatsFromRoom(
        logTag: String = "Delete Heating Stats",
        heatingStatsIds: List<Long>
    ): Int {
        Log.i(logTag, "Deleting heating stats with IDs $heatingStatsIds from Room Defroster Database.")
        if (heatingStatsIds.isEmpty()) {
            Log.i(logTag, "No heating stats to delete.")
            return 0
        }
        val deletedHeatingStatsCount = heatingStatsDao.deleteByIds(heatingStatsIds)
        Log.i(logTag, "Deleted heating stats. Count: $deletedHeatingStatsCount.")
        return deletedHeatingStatsCount
    }

    fun deleteHeatingStats(heatingStatsIds: List<Long>) {
        if (heatingStatsIds.isEmpty()) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            deleteHeatingStatsFromRoom(heatingStatsIds = heatingStatsIds)
        }
    }
}
