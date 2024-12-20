package com.jurjandreigeorge.defroster.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jurjandreigeorge.defroster.data.HeatingStats

@Database(entities = [HeatingStats::class], exportSchema = false, version = 1)
abstract class RoomDefrosterDatabase : RoomDatabase() {
    abstract fun heatingStatsDao(): HeatingStatsDao
}