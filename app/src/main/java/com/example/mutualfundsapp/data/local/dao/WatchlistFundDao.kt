package com.example.mutualfundsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mutualfundsapp.data.local.entity.WatchlistFundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistFundDao {

    @Query("SELECT * FROM watchlist_funds WHERE watchlistId = :watchlistId")
    fun getFundsForWatchlist(watchlistId: String): Flow<List<WatchlistFundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFund(fund: WatchlistFundEntity)

    @Query("DELETE FROM watchlist_funds WHERE watchlistId = :watchlistId AND schemeCode = :schemeCode")
    suspend fun deleteFund(watchlistId: String, schemeCode: String)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_funds WHERE schemeCode = :schemeCode)")
    fun isFundInAnyWatchlist(schemeCode: String): Flow<Boolean>

    @Query("SELECT * FROM watchlist_funds")
    fun getAllFunds(): Flow<List<WatchlistFundEntity>>
}