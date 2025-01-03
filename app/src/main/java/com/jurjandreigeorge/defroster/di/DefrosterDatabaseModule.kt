package com.jurjandreigeorge.defroster.di

import android.content.Context
import androidx.room.Room
import com.jurjandreigeorge.defroster.data.room.RoomDefrosterDatabase
import com.jurjandreigeorge.defroster.data.room.HeatingStatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DefrosterDatabaseModule {
    @Provides
    @Singleton
    fun provideDefrosterDatabase(
        @ApplicationContext applicationContext: Context
    ): RoomDefrosterDatabase = Room.databaseBuilder(
        applicationContext,
        RoomDefrosterDatabase::class.java,
        "defroster-database"
    ).build()

    @Provides
    @Singleton
    fun provideDefrosterDatabaseHeatingStatsDao(
        roomDefrosterDatabase: RoomDefrosterDatabase
    ): HeatingStatsDao = roomDefrosterDatabase.heatingStatsDao()
}
