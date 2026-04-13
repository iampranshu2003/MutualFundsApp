package com.example.mutualfundsapp.presentation.watchlist

import com.example.mutualfundsapp.domain.model.FundSummary
import com.example.mutualfundsapp.domain.model.Watchlist

data class WatchlistState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val watchlists: List<Watchlist> = emptyList(),
    val selectedWatchlistFunds: List<FundSummary> = emptyList()
)

sealed class WatchlistEvent {
    object LoadWatchlists : WatchlistEvent()
    data class OpenWatchlist(val watchlistId: String) : WatchlistEvent()
    object Retry : WatchlistEvent()
    data class CreateWatchlist(val name: String) : WatchlistEvent()
    data class RemoveFund(val watchlistId: String, val schemeCode: String) : WatchlistEvent()
    data class LoadWatchlistFunds(val watchlistId: String) : WatchlistEvent()
    object NavigateToExplore : WatchlistEvent()
}

sealed class WatchlistEffect {
    data class NavigateToFolder(val watchlistId: String) : WatchlistEffect()
    object NavigateToExplore : WatchlistEffect()
}