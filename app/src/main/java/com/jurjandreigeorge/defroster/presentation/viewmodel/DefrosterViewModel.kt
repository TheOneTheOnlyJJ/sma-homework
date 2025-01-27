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
    var hasAmbientTempSensor by mutableStateOf(false)
    var currentTemp by mutableFloatStateOf(0f)
    var targetTemp by mutableIntStateOf(10)
    private val targetTempTolerance = 2
    val targetTempLowerLimit by derivedStateOf { this.targetTemp - this.targetTempTolerance }
    val targetTempUpperLimit by derivedStateOf { this.targetTemp + this.targetTempTolerance }
    var heatingState by mutableStateOf(HeatingState.NOT_HEATING)
        private set
    private val heatingThreadIterationCycleLoopCount = 100_000_000L
    private val heatingThreadSleepTimeMs = 5_000L
    private val heatingThreads = mutableStateListOf<Thread>()

    private val heatingStatsTrackerCurrentTempSamplingPeriodSeconds = 5L
    private val heatingStatsTracker: HeatingStatsTracker = HeatingStatsTracker(
        getCurrentTemp = { this.currentTemp },
        currentTempSamplingPeriodSeconds = this.heatingStatsTrackerCurrentTempSamplingPeriodSeconds
    )
    val heatingStatsCountThreshold = 1
    var hasWarnedUserOfHeatingStatsCountThreshold by mutableStateOf(false)

    val allHeatingStatsFlow: Flow<List<HeatingStatsEntity>> = this.heatingStatsDao.loadAllFlow()
    val heatingStatsCountFlow: Flow<Int> = this.heatingStatsDao.loadCountFlow()

    fun toggleHeating() {
        when (this.heatingState) {
            HeatingState.NOT_HEATING -> {
                this.startHeating()
            }
            HeatingState.HEATING -> {
                this.stopHeating()
            }
            else -> {
                throw RuntimeException(
                    "Should not be possible to toggle heating while heating state is ${this.heatingState}!"
                )
            }
        }
    }

    private fun runHeatingCycle() {
        var meaninglessCounter = 0L
        for (j in 0..this.heatingThreadIterationCycleLoopCount) {
            meaninglessCounter += 1L
        }
    }

    private fun heatingThreadImpl(logTag: String) {
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
                    Log.i(logTag, "Going to sleep ${this.heatingThreadSleepTimeMs} ms.")
                    Thread.sleep(this.heatingThreadSleepTimeMs)
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
        this.heatingState = HeatingState.STARTING_HEATING
        CoroutineScope(Dispatchers.Default).launch {
            this@DefrosterViewModel.heatingStatsTracker.startTracking(
                targetTemp = this@DefrosterViewModel.targetTemp,
                isTempTracked = this@DefrosterViewModel.hasAmbientTempSensor
            )
            val availableProcessors = Runtime.getRuntime().availableProcessors()
            Log.i("Defroster", "Device has $availableProcessors available processors.")
            for (i in 1..availableProcessors) {
                val newHeatingThread = Thread {
                    this@DefrosterViewModel.heatingThreadImpl("Defroster Heating Thread No. $i")
                }
                this@DefrosterViewModel.heatingThreads.add(newHeatingThread)
            }
            for (i in 0..< this@DefrosterViewModel.heatingThreads.size) {
                this@DefrosterViewModel.heatingThreads[i].start()
            }
            Log.i("Defroster", "All heating threads started.")
            this@DefrosterViewModel.heatingState = HeatingState.HEATING
        }

    }

    private fun stopHeating() {
        Log.i("Defroster", "Stopping heating.")
        this.heatingState = HeatingState.STOPPING_HEATING
        CoroutineScope(Dispatchers.Default).launch {
            Log.i("Defroster", "Interrupting all heating threads.")
            for (thread in this@DefrosterViewModel.heatingThreads) {
                thread.interrupt()
            }
            Log.i("Defroster", "Joining all heating threads.")
            for (thread in this@DefrosterViewModel.heatingThreads) {
                thread.join()
            }
            this@DefrosterViewModel.heatingThreads.clear()
            Log.i("Defroster", "All heating threads stopped.")
            val heatingStats = this@DefrosterViewModel.heatingStatsTracker.stopTracking()
            Log.i("Defroster", "Heating stats: ${heatingStats}.")
            this@DefrosterViewModel.heatingState = HeatingState.NOT_HEATING
            CoroutineScope(Dispatchers.IO).launch {
                this@DefrosterViewModel.insertHeatingStatsIntoRoom(
                    heatingStats = listOf(heatingStats)
                )
            }
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
        val insertedRowIds = this.heatingStatsDao.insertAll(heatingStats)
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
        val deletedHeatingStatsCount = this.heatingStatsDao.deleteByIds(heatingStatsIds)
        Log.i(logTag, "Deleted heating stats. Count: $deletedHeatingStatsCount.")
        return deletedHeatingStatsCount
    }

    fun deleteHeatingStats(heatingStatsIds: List<Long>) {
        if (heatingStatsIds.isEmpty()) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            this@DefrosterViewModel.deleteHeatingStatsFromRoom(heatingStatsIds = heatingStatsIds)
        }
    }
}
