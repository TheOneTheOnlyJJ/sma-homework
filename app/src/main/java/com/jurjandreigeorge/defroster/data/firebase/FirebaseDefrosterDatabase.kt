package com.jurjandreigeorge.defroster.data.firebase

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.jurjandreigeorge.defroster.data.HeatingStats


fun HeatingStats.toFirebaseObject(): Map<String, Any> {
    return mapOf(
        "id" to this.id,
        "startTime" to this.startTime,
        "endTime" to this.endTime,
        "startTemp" to this.startTemp,
        "targetTemp" to this.targetTemp,
        "endTemp" to this.endTemp
    )
}

class FirebaseDefrosterDatabase {
    private val logTag = "Firebase Defroster Database"
    private val database = FirebaseDatabase.getInstance(
        "https://defroster-a0529-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference

    init {
        Log.i("Firebase Defroster Database", "Initialised Firebase Defroster Database.")
    }

    fun addHeatingStats(
        heatingStats: List<HeatingStats>,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
        onCancelled: () -> Unit = {}
    ) {
        if (heatingStats.isEmpty()) {
            Log.i(this.logTag, "No heating stats to add.")
            return
        }
        val childrenToAdd = heatingStats.associate { heatingStatsEntry ->
            "/heatingStats/${heatingStatsEntry.id}" to heatingStatsEntry.toFirebaseObject()
        }
        this.database
            .updateChildren(childrenToAdd)
            .addOnSuccessListener {
                Log.i(this.logTag,"Heating stats added to Firebase Defroster Database successfully.")
                onSuccess()
            }
            .addOnFailureListener {
                Log.w(this.logTag,"Failed to add heating stats to Firebase Defroster Database.")
                onFailure()
            }
            .addOnCanceledListener {
                Log.w(this.logTag, "Cancelled adding heating stats to Firebase Defroster Database.")
                onCancelled()
            }
    }

    fun deleteHeatingStats(
        heatingStatsIds: List<Long>,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
        onCancelled: () -> Unit = {}
    ) {
        if (heatingStatsIds.isEmpty()) {
            Log.i(this.logTag, "No heating stats to delete.")
            return
        }
        val childrenToDelete = heatingStatsIds.associate { heatingStatsId ->
            "/heatingStats/$heatingStatsId" to null
        }
        this.database
            .updateChildren(childrenToDelete)
            .addOnSuccessListener {
                Log.i(this.logTag, "Heating stats deleted from Firebase Defroster Database successfully.")
                onSuccess()
            }
            .addOnFailureListener {
                Log.w(this.logTag, "Failed to delete heating stats from Firebase Defroster Database.")
                onFailure()
            }
            .addOnCanceledListener {
                Log.w(this.logTag, "Cancelled deleting heating stats from Firebase Defroster Database.")
                onCancelled()
            }
    }
}