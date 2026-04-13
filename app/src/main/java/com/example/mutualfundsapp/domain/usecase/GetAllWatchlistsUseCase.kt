package com.example.mutualfundsapp.domain.usecase

import com.example.mutualfundsapp.domain.repository.WatchlistRepository
import javax.inject.Inject

class GetAllWatchlistsUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    operator fun invoke() = repository.getAllWatchlists()
}
