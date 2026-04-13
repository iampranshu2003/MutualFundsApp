package com.example.mutualfundsapp.domain.repository

import com.example.mutualfundsapp.domain.model.FundDetail
import com.example.mutualfundsapp.domain.model.FundSummary


interface MutualFundsRepository {
    suspend fun searchFunds(query: String): Result<List<FundSummary>>
    suspend fun getFundDetail(schemeCode: String): Result<FundDetail>
    suspend fun getExploreCategories(): Result<Map<String, List<FundSummary>>>
}