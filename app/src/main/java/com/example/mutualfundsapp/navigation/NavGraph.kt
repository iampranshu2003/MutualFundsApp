package com.example.mutualfundsapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.mutualfundsapp.presentation.explore.ExploreRoute
import com.example.mutualfundsapp.presentation.funddetail.FundDetailRoute
import com.example.mutualfundsapp.presentation.search.SearchRoute
import com.example.mutualfundsapp.presentation.viewall.ViewAllRoute
import com.example.mutualfundsapp.presentation.watchlist.WatchlistFolderRoute
import com.example.mutualfundsapp.presentation.watchlist.WatchlistRoute

@Composable
fun RootNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "explore_root",
        modifier = modifier
    ) {
        composable("explore_root") {
            ExploreNavGraph()
        }
        composable("watchlist_root") {
            WatchlistNavGraph(
                onNavigateToExploreTab = { navController.navigate("explore_root") }
            )
        }
    }
}

@Composable
fun ExploreNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Explore.route
    ) {
        composable(Screen.Explore.route) {
            ExploreRoute(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToViewAll = { category ->
                    navController.navigate(Screen.ViewAll("").createRoute(category))
                },
                onNavigateToFundDetail = { schemeCode ->
                    navController.navigate(Screen.FundDetail("").createRoute(schemeCode))
                }
            )
        }

        composable(Screen.Search.route) {
            SearchRoute(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFundDetail = { schemeCode ->
                    navController.navigate(Screen.FundDetail("").createRoute(schemeCode))
                }
            )
        }

        composable(Screen.ViewAll("{category}").route) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category").orEmpty()
            ViewAllRoute(
                category = category,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFundDetail = { schemeCode ->
                    navController.navigate(Screen.FundDetail("").createRoute(schemeCode))
                }
            )
        }

        composable(Screen.FundDetail("{schemeCode}").route) { backStackEntry ->
            val schemeCode = backStackEntry.arguments?.getString("schemeCode").orEmpty()
            FundDetailRoute(
                schemeCode = schemeCode,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun WatchlistNavGraph(
    onNavigateToExploreTab: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Watchlist.route
    ) {
        composable(Screen.Watchlist.route) {
            WatchlistRoute(
                onNavigateToFolder = { id ->
                    navController.navigate(Screen.WatchlistFolder("").createRoute(id))
                },
                onNavigateToExplore = onNavigateToExploreTab
            )
        }

        composable(Screen.WatchlistFolder("{watchlistId}").route) { backStackEntry ->
            val watchlistId = backStackEntry.arguments?.getString("watchlistId").orEmpty()
            WatchlistFolderRoute(
                watchlistId = watchlistId,
                onNavigateToExplore = onNavigateToExploreTab,
                onNavigateToFundDetail = { schemeCode ->
                    navController.navigate(Screen.FundDetail("").createRoute(schemeCode))
                }
            )
        }

        composable(Screen.FundDetail("{schemeCode}").route) { backStackEntry ->
            val schemeCode = backStackEntry.arguments?.getString("schemeCode").orEmpty()
            FundDetailRoute(
                schemeCode = schemeCode,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
