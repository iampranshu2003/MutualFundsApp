package com.example.mutualfundsapp.domain.usecase

import com.example.mutualfundsapp.domain.repository.MutualFundsRepository
import javax.inject.Inject

class SearchFundsUseCase @Inject constructor(
    private val repository: MutualFundsRepository
) {
    suspend operator fun invoke(query: String) = repository.searchFunds(query)
}