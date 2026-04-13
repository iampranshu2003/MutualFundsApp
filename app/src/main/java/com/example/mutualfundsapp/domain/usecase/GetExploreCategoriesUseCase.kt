package com.example.mutualfundsapp.domain.usecase

import com.example.mutualfundsapp.domain.repository.MutualFundsRepository
import javax.inject.Inject

class GetExploreCategoriesUseCase @Inject constructor(
    private val repository: MutualFundsRepository
) {
    suspend operator fun invoke() = repository.getExploreCategories()
}