package com.jurjandreigeorge.defroster.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jurjandreigeorge.defroster.data.HeatingStats
import kotlinx.coroutines.flow.Flow

@Dao
interface HeatingStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(heatingStats: HeatingStats): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(heatingStats: List<HeatingStats>): List<Long>

    @Query("SELECT id FROM heating_stats WHERE rowid IN (:rowIds)")
    fun getIdsByRowIds(rowIds: List<Long>): List<Long>

    @Query("SELECT * FROM heating_stats")
    fun loadAllFlow(): Flow<List<HeatingStats>>

    @Query("SELECT * FROM heating_stats WHERE is_deleted = 0")
    fun loadAllNonDeletedFlow(): Flow<List<HeatingStats>>

    @Query("SELECT * FROM heating_stats WHERE is_deleted = 1")
    fun loadAllSoftDeletedFlow(): Flow<List<HeatingStats>>

    @Query("SELECT * FROM heating_stats WHERE is_synced = 0")
    fun loadAllUnsyncedFlow(): Flow<List<HeatingStats>>

    @Query("SELECT id FROM heating_stats WHERE is_synced = 0 AND is_deleted = 0")
    fun loadAllUnsyncedAndNonDeletedIds(): List<Long>

    @Query("SELECT * FROM heating_stats WHERE is_synced = 0 AND is_deleted = 1")
    fun loadAllUnsyncedAndSoftDeleted(): List<HeatingStats>

    @Query("SELECT id FROM heating_stats WHERE is_synced = 0 AND id IN (:heatingStatsIds)")
    fun getUnsyncedIdsByIds(heatingStatsIds: List<Long>): List<Long>

    @Query("SELECT * FROM heating_stats WHERE is_synced = 1")
    fun loadAllSyncedFlow(): Flow<List<HeatingStats>>

    @Query("SELECT id FROM heating_stats WHERE is_synced = 1 AND is_deleted = 1")
    fun loadAllSyncedAndSoftDeletedIds(): List<Long>

    @Query("SELECT id FROM heating_stats WHERE is_synced = 1 AND id IN (:heatingStatsIds)")
    fun getSyncedIdsByIds(heatingStatsIds: List<Long>): List<Long>

    @Query("SELECT * FROM heating_stats WHERE id IN (:heatingStatsIds)")
    fun loadByIds(heatingStatsIds: List<Long>): List<HeatingStats>

    @Query("DELETE FROM heating_stats WHERE id IN (:heatingStatsIds)")
    fun deleteByIds(heatingStatsIds: List<Long>): Int

    @Query("UPDATE heating_stats SET is_synced = 1 WHERE id IN (:heatingStatsIds)")
    fun markAsSyncedByIds(heatingStatsIds: List<Long>): Int

    @Query("UPDATE heating_stats SET is_deleted = 1 WHERE id IN (:heatingStatsIds)")
    fun markAsSoftDeletedByIds(heatingStatsIds: List<Long>): Int
}