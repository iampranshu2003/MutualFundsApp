package com.example.mutualfundsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mutualfundsapp.data.local.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM watchlists ORDER BY createdAt DESC")
    fun getAllWatchlists(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(watchlist: WatchlistEntity)

    @Query("DELETE FROM watchlists WHERE id = :watchlistId")
    suspend fun deleteWatchlist(watchlistId: String)
}
