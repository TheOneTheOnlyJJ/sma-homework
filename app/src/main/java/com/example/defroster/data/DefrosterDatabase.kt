package com.example.defroster.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HeatingStats::class], exportSchema = false, version = 1)
abstract class DefrosterDatabase : RoomDatabase() {
    abstract fun heatingStatsDao(): HeatingStatsDao
}