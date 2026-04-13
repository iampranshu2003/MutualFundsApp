package com.example.mutualfundsapp.domain.usecase

import com.example.mutualfundsapp.domain.repository.WatchlistRepository
import javax.inject.Inject

class IsFundInAnyWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    operator fun invoke(schemeCode: String) = repository.isFundInAnyWatchlist(schemeCode)
}