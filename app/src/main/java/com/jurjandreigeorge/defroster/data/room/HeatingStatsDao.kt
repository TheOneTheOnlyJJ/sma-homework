package com.jurjandreigeorge.defroster.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jurjandreigeorge.defroster.data.HeatingStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeatingStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(heatingStats: HeatingStatsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(heatingStats: List<HeatingStatsEntity>): List<Long>

    @Query("SELECT id FROM heating_stats WHERE rowid IN (:rowIds)")
    fun getIdsByRowIds(rowIds: List<Long>): List<Long>

    @Query("SELECT * FROM heating_stats")
    fun loadAllFlow(): Flow<List<HeatingStatsEntity>>

    @Query("SELECT * FROM heating_stats WHERE id IN (:heatingStatsIds)")
    fun loadByIds(heatingStatsIds: List<Long>): List<HeatingStatsEntity>

    @Query("DELETE FROM heating_stats WHERE id IN (:heatingStatsIds)")
    fun deleteByIds(heatingStatsIds: List<Long>): Int
}
