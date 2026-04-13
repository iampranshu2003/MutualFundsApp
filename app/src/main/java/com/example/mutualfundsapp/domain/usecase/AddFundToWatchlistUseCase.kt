package com.example.mutualfundsapp.domain.usecase

import com.example.mutualfundsapp.domain.model.FundSummary
import com.example.mutualfundsapp.domain.repository.WatchlistRepository
import javax.inject.Inject

class AddFundToWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    suspend operator fun invoke(watchlistId: String, fund: FundSummary) =
        repository.addFundToWatchlist(watchlistId, fund)
}