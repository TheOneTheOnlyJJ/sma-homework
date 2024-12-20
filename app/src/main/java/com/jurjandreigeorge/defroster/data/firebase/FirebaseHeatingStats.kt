package com.jurjandreigeorge.defroster.data.firebase

import com.google.firebase.database.IgnoreExtraProperties
import com.jurjandreigeorge.defroster.data.HeatingStats

@IgnoreExtraProperties
data class FirebaseHeatingStats(
    val id: Long? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val startTemp: Float? = null,
    val targetTemp: Int? = null,
    val endTemp: Float? = null
) {
    fun toHeatingStats(): HeatingStats {
        return HeatingStats(
            id = this.id ?: 0,
            startTime = this.startTime.orEmpty(),
            endTime = this.endTime.orEmpty(),
            startTemp = this.startTemp ?: 0.0f,
            targetTemp = this.targetTemp ?: 0,
            endTemp = this.endTemp ?: 0.0f
        )
    }
}