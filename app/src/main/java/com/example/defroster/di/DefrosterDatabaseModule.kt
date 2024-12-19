package com.example.defroster.di

import android.content.Context
import androidx.room.Room
import com.example.defroster.data.DefrosterDatabase
import com.example.defroster.data.HeatingStatsDao
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
    ): DefrosterDatabase = Room.databaseBuilder(
        applicationContext,
        DefrosterDatabase::class.java,
        "defroster-database"
    ).build()

    @Provides
    @Singleton
    fun provideDefrosterDatabaseHeatingStatsDao(
        defrosterDatabase: DefrosterDatabase
    ): HeatingStatsDao = defrosterDatabase.heatingStatsDao()
}