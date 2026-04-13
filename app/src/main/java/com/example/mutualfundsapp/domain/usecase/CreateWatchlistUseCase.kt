package com.example.mutualfundsapp.domain.usecase

import com.example.mutualfundsapp.domain.repository.WatchlistRepository
import javax.inject.Inject

class CreateWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository
) {
    suspend operator fun invoke(name: String) = repository.createWatchlist(name)
}