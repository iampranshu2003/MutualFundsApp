package com.example.mutualfundsapp.domain.usecase

import com.example.mutualfundsapp.domain.repository.WatchlistRepository
import javax.inject.Inject

class RemoveFundFromWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    suspend operator fun invoke(watchlistId: String, schemeCode: String) =
        repository.removeFundFromWatchlist(watchlistId, schemeCode)
}
