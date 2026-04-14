package com.example.mutualfundsapp.presentation.search

import com.example.mutualfundsapp.domain.model.FundSummary

data class SearchState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val results: List<FundSummary> = emptyList()
)

sealed class SearchEvent {
    data class UpdateQuery(val query: String) : SearchEvent()
    object Retry : SearchEvent()
    data class OpenFund(val schemeCode: String) : SearchEvent()
}

sealed class SearchEffect {
    data class NavigateToFundDetail(val schemeCode: String) : SearchEffect()
}