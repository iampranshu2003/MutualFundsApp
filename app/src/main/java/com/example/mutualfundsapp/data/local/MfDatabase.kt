package com.example.mutualfundsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mutualfundsapp.data.local.dao.WatchlistDao
import com.example.mutualfundsapp.data.local.dao.WatchlistFundDao
import com.example.mutualfundsapp.data.local.entity.WatchlistEntity
import com.example.mutualfundsapp.data.local.entity.WatchlistFundEntity

@Database(
    entities = [WatchlistEntity::class, WatchlistFundEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MfDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
    abstract fun watchlistFundDao(): WatchlistFundDao
}