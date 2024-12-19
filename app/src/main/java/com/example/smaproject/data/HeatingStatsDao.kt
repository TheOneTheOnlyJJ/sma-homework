package com.example.smaproject.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HeatingStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(heatingStats: HeatingStats): Long
    @Query("SELECT * FROM heating_stats")
    fun loadAll(): Flow<List<HeatingStats>>
    @Delete
    fun delete(vararg heatingStats: HeatingStats): Int
}