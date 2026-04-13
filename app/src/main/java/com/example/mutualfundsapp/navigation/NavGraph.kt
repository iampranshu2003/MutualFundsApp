package com.example.mutualfundsapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "explore_root",
        modifier = modifier
    ) {
        navigation(startDestination = Screen.Explore.route, route = "explore_root") {
            composable(Screen.Explore.route) {
                ExploreScreen(
                    state = ,
                    onEvent =
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    state = ,
                    onEvent =
                )
            }
            composable(Screen.ViewAll("{category}").route) {
                ViewAllScreen(
                    state = ,
                    onEvent =
                )
            }
            composable(Screen.FundDetail("{schemeCode}").route) {
                FundDetailScreen(
                    state =,
                    onEvent =
                )
            }
        }

        navigation(startDestination = Screen.Watchlist.route, route = "watchlist_root") {
            composable(Screen.Watchlist.route) {
                WatchlistScreen(
                    state = ,
                    onEvent =
                )
            }
            composable(Screen.WatchlistFolder("{watchlistId}").route) {
                WatchlistFolderScreen(
                    state = ,
                    onEvent =
                )
            }
        }
    }
}
