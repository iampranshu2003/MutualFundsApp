package com.example.mutualfundsapp.navigation

sealed class Screen(val route: String) {
    object Explore : Screen("explore")
    object Search : Screen("search")
    object Watchlist : Screen("watchlist")

    data class ViewAll(val category: String) : Screen("view_all/{category}") {
        fun createRoute(category: String) = "view_all/$category"
    }

    data class FundDetail(val schemeCode: String) : Screen("fund_detail/{schemeCode}") {
        fun createRoute(schemeCode: String) = "fund_detail/$schemeCode"
    }

    data class WatchlistFolder(val watchlistId: String) : Screen("watchlist_folder/{watchlistId}") {
        fun createRoute(watchlistId: String) = "watchlist_folder/$watchlistId"
    }
}
