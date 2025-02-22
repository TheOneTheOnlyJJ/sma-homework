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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.jurjandreigeorge.defroster.data.HeatingStats
import com.jurjandreigeorge.defroster.data.firebase.FirebaseDefrosterDatabase
import com.jurjandreigeorge.defroster.data.firebase.FirebaseHeatingStats
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
    private val heatingThreadSleepTime = 5_000L
    private val heatingThreads = mutableStateListOf<Thread>()
    private val heatingStatsTracker: HeatingStatsTracker = HeatingStatsTracker()
    val nonDeletedHeatingStatsFlow: Flow<List<HeatingStats>> = heatingStatsDao.loadAllNonDeletedFlow()

    private val firebaseDatabase = FirebaseDefrosterDatabase()
    var latestDefrost by mutableStateOf<HeatingStats?>(null)
    private val latestDefrostListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.i("Latest Defrost Listener", "Latest defrost changed.")
            val rawData = dataSnapshot.getValue(FirebaseHeatingStats::class.java)
            if (rawData != null) {
                latestDefrost = rawData.toHeatingStats()
            } else {
                latestDefrost = null
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(
                "Latest Defrost Listener",
                "Error reading latest defrost data ${databaseError.toException()}."
            )
        }
    }

    var isHeatingCardListReversed by mutableStateOf(false)

    init {
        this.syncRoomDefrosterDatabaseWithFirebaseDefrosterDatabaseOfflineDifferences()
        this.firebaseDatabase.getLatestDefrostDatabaseReference().addValueEventListener(
            this.latestDefrostListener
        )
        Log.i("Defroster", "Initialised DefrosterViewModel.")
    }

    private fun syncRoomDefrosterDatabaseWithFirebaseDefrosterDatabaseOfflineDifferences() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Database Sync", "Start sync between Room and Firebase Defroster Database.")
            val unsyncedAndNonDeletedIds = heatingStatsDao.loadAllUnsyncedAndNonDeletedIds()
            firebaseDatabase.addHeatingStats(
                heatingStats = heatingStatsDao.loadByIds(unsyncedAndNonDeletedIds),
                onSuccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        markHeatingStatsAsSynced("Mark As Synced", unsyncedAndNonDeletedIds)
                    }
                }
            )
            val syncedAndSoftDeletedIds = heatingStatsDao.loadAllSyncedAndSoftDeletedIds()
            firebaseDatabase.deleteHeatingStats(
                heatingStatsIds = syncedAndSoftDeletedIds,
                onSuccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        deleteHeatingStatsFromRoom("Delete Synced", syncedAndSoftDeletedIds)
                    }
                }
            )
        }
    }

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
        this.heatingState = HeatingState.NOT_HEATING
        CoroutineScope(Dispatchers.IO).launch {
            val insertedIds = heatingStatsDao.getIdsByRowIds(
                insertHeatingStatsIntoRoom("Insert Heating Stats", listOf(heatingStats))
            )
            firebaseDatabase.addHeatingStats(
                heatingStats = heatingStatsDao.loadByIds(insertedIds),
                onSuccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        markHeatingStatsAsSynced("Mark As Synced", insertedIds)
                    }
                }
            )
        }
        firebaseDatabase.updateLatestDefrost(
            heatingStats = heatingStats
        )
    }

    private fun markHeatingStatsAsSynced(logTag: String, heatingStatsIds: List<Long>): Int {
        Log.i(logTag, "Marking as synced heating stats with IDs: $heatingStatsIds.")
        if (heatingStatsIds.isEmpty()) {
            Log.i(logTag, "No heating stats to mark as synced.")
            return 0
        }
        val markedAsSyncedCount = heatingStatsDao.markAsSyncedByIds(heatingStatsIds)
        Log.i(logTag, "Heating stats marked as synced. Count: $markedAsSyncedCount.")
        return markedAsSyncedCount
    }

    private fun markHeatingStatsAsDeleted(logTag: String, heatingStatsIds: List<Long>): Int {
        Log.i(logTag, "Marking as deleted heating stats with IDs: $heatingStatsIds.")
        if (heatingStatsIds.isEmpty()) {
            Log.i(logTag, "No heating stats to mark as deleted.")
            return 0
        }
        val markedAsDeletedCount = heatingStatsDao.markAsSoftDeletedByIds(heatingStatsIds)
        Log.i(logTag, "Marked heating stats as deleted. Count: $markedAsDeletedCount.")
        return markedAsDeletedCount
    }

    private fun insertHeatingStatsIntoRoom(logTag: String, heatingStats: List<HeatingStats>): List<Long> {
        Log.i(logTag, "Inserting heating stats with IDs ${heatingStats.map { it.id }} into Room Defroster Database.")
        if (heatingStats.isEmpty()) {
            Log.i(logTag, "No heating stats to insert.")
            return emptyList()
        }
        val insertedRowIds = heatingStatsDao.insertAll(heatingStats)
        Log.i(logTag, "Inserted heating stats. Inserted row IDs: $insertedRowIds.")
        return insertedRowIds
    }

    private fun deleteHeatingStatsFromRoom(logTag: String, heatingStatsIds: List<Long>): Int {
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
            markHeatingStatsAsDeleted("Mark as Deleted", heatingStatsIds)
            CoroutineScope(Dispatchers.IO).launch {
                deleteHeatingStatsFromRoom("Delete Unsynced", heatingStatsDao.getUnsyncedIdsByIds(heatingStatsIds))
            }
            val syncedHeatingStatsIds = heatingStatsDao.getSyncedIdsByIds(heatingStatsIds)
            firebaseDatabase.deleteHeatingStats(
                heatingStatsIds = syncedHeatingStatsIds,
                onSuccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        deleteHeatingStatsFromRoom("Delete Synced", syncedHeatingStatsIds)
                    }
                }
            )
        }
    }
}
