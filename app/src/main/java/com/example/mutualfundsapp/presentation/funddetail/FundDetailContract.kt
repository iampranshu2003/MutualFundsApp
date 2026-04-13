package com.example.mutualfundsapp.presentation.funddetail

import com.example.mutualfundsapp.domain.model.NavPoint
import com.example.mutualfundsapp.domain.model.Watchlist

data class FundDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val schemeCode: String = "",
    val schemeName: String = "",
    val amcName: String = "",
    val schemeType: String = "",
    val nav: String = "",
    val navChangeValue: Float = 0f,
    val navChangePercent: Float = 0f,
    val navChangePositive: Boolean = true,
    val fullHistory: List<NavPoint> = emptyList(),
    val filteredHistory: List<NavPoint> = emptyList(),
    val selectedRange: NavRange = NavRange.ALL,
    val isInWatchlist: Boolean = false,
    val watchlists: List<Watchlist> = emptyList(),
    val watchlistSelections: Map<String, Boolean> = emptyMap(),
    val showBottomSheet: Boolean = false
)

enum class NavRange(val days: Int?) {
    ONE_MONTH(30),
    THREE_MONTHS(90),
    SIX_MONTHS(180),
    ONE_YEAR(365),
    ALL(null)
}

sealed class FundDetailEvent {
    data class Load(val schemeCode: String) : FundDetailEvent()
    data class SelectRange(val range: NavRange) : FundDetailEvent()
    object ToggleBookmark : FundDetailEvent()
    object CloseBottomSheet : FundDetailEvent()
    data class ToggleWatchlist(val watchlistId: String, val checked: Boolean) : FundDetailEvent()
    data class CreateWatchlist(val name: String) : FundDetailEvent()
    object ApplyWatchlistChanges : FundDetailEvent()
    object Retry : FundDetailEvent()
}

sealed class FundDetailEffect {
    data class ShowMessage(val messageRes: Int, val formatArg: String? = null) : FundDetailEffect()
}
