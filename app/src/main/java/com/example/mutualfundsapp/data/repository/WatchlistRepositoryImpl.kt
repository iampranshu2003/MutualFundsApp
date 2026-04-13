package com.example.mutualfundsapp.data.repository

import com.example.mutualfundsapp.data.local.dao.WatchlistDao
import com.example.mutualfundsapp.data.local.dao.WatchlistFundDao
import com.example.mutualfundsapp.data.local.entity.WatchlistEntity
import com.example.mutualfundsapp.data.local.entity.WatchlistFundEntity
import com.example.mutualfundsapp.domain.model.FundSummary
import com.example.mutualfundsapp.domain.model.Watchlist
import com.example.mutualfundsapp.domain.repository.WatchlistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepositoryImpl @Inject constructor(
    private val watchlistDao: WatchlistDao,
    private val watchlistFundDao: WatchlistFundDao
) : WatchlistRepository {

    override fun getAllWatchlists(): Flow<List<Watchlist>> {
        return combine(
            watchlistDao.getAllWatchlists(),
            watchlistFundDao.getAllFunds()
        ) { watchlists, funds ->
            watchlists.map { wl ->
                val wlFunds = funds.filter { it.watchlistId == wl.id }
                Watchlist(
                    id = wl.id,
                    name = wl.name,
                    funds = wlFunds.map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun createWatchlist(name: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            watchlistDao.insertWatchlist(
                WatchlistEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun addFundToWatchlist(
        watchlistId: String,
        fund: FundSummary
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            watchlistFundDao.insertFund(
                WatchlistFundEntity(
                    watchlistId = watchlistId,
                    schemeCode = fund.schemeCode,
                    schemeName = fund.schemeName,
                    nav = fund.nav
                )
            )
        }
    }

    override suspend fun removeFundFromWatchlist(
        watchlistId: String,
        schemeCode: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            watchlistFundDao.deleteFund(watchlistId, schemeCode)
        }
    }

    override fun isFundInAnyWatchlist(schemeCode: String): Flow<Boolean> {
        return watchlistFundDao.isFundInAnyWatchlist(schemeCode)
    }
}

private fun WatchlistFundEntity.toDomain(): FundSummary {
    return FundSummary(
        schemeCode = schemeCode,
        schemeName = schemeName,
        nav = nav
    )
}