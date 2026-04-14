package com.example.mutualfundsapp.domain.usecase

import com.example.mutualfundsapp.domain.repository.MutualFundsRepository
import javax.inject.Inject

class GetAllFundsUseCase @Inject constructor(
    private val repository: MutualFundsRepository
) {
    suspend operator fun invoke(limit: Int, offset: Int) = repository.getAllFunds(limit, offset)
}
