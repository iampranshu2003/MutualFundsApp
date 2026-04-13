package com.example.mutualfundsapp.domain.repository

import com.example.mutualfundsapp.domain.model.FundSummary
import com.example.mutualfundsapp.domain.model.Watchlist
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {

    fun getAllWatchlists(): Flow<List<Watchlist>>
    suspend fun createWatchlist(name: String): Result<Unit>
    suspend fun addFundToWatchlist(watchlistId: String, fund: FundSummary): Result<Unit>
    suspend fun removeFundFromWatchlist(watchlistId: String, schemeCode: String): Result<Unit>
    fun isFundInAnyWatchlist(schemeCode: String): Flow<Boolean>
}