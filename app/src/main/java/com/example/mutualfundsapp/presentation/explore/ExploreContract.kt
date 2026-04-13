package com.example.mutualfundsapp.presentation.explore

import com.example.mutualfundsapp.domain.model.FundSummary

data class ExploreState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val categories: Map<String, List<FundSummary>> = emptyMap()
)

sealed class ExploreEvent {
    object LoadExplore : ExploreEvent()
    object Retry : ExploreEvent()
    data class OpenFund(val schemeCode: String) : ExploreEvent()
    object OpenSearch : ExploreEvent()
    data class OpenCategory(val category: String) : ExploreEvent()
}

sealed class ExploreEffect {
    data class NavigateToFundDetail(val schemeCode: String) : ExploreEffect()
    object NavigateToSearch : ExploreEffect()
    data class NavigateToViewAll(val category: String) : ExploreEffect()
}