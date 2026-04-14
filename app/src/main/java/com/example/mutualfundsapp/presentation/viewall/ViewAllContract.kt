package com.example.mutualfundsapp.presentation.viewall

import com.example.mutualfundsapp.domain.model.FundSummary

data class ViewAllState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val category: String = "",
    val allFunds: List<FundSummary> = emptyList(),
    val visibleFunds: List<FundSummary> = emptyList(),
    val loadedCount: Int = 0,
    val pageSize: Int = 10,
    val hasMore: Boolean = true
)

sealed class ViewAllEvent {
    data class Load(val category: String) : ViewAllEvent()
    object LoadNextPage : ViewAllEvent()
    object Retry : ViewAllEvent()
    data class OpenFund(val schemeCode: String) : ViewAllEvent()
}

sealed class ViewAllEffect {
    data class NavigateToFundDetail(val schemeCode: String) : ViewAllEffect()
}