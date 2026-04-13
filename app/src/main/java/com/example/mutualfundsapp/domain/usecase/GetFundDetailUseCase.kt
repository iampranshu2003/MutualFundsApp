package com.example.mutualfundsapp.domain.usecase

import com.example.mutualfundsapp.domain.repository.MutualFundsRepository
import javax.inject.Inject

class GetFundDetailUseCase @Inject constructor(
    private val repository: MutualFundsRepository
) {
    suspend operator fun invoke(schemeCode: String) = repository.getFundDetail(schemeCode)
}