package com.example.mutualfundsapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.mutualfundsapp.data.local.MfDatabase
import com.example.mutualfundsapp.data.local.dao.WatchlistDao
import com.example.mutualfundsapp.data.local.dao.WatchlistFundDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MfDatabase {
        return Room.databaseBuilder(
            context,
            MfDatabase::class.java,
            "mf_explorer.db"
        ).build()
    }

    @Provides
    fun provideWatchlistDao(db: MfDatabase): WatchlistDao = db.watchlistDao()

    @Provides
    fun provideWatchlistFundDao(db: MfDatabase): WatchlistFundDao = db.watchlistFundDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("mf_explorer_prefs") }
        )
    }
}
