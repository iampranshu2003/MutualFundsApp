package com.example.mutualfundsapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val rootNav = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = rootNav.currentBackStackEntryAsState().value?.destination?.route
                NavigationBarItem(
                    selected = currentRoute == "explore_root",
                    onClick = { rootNav.navigate("explore_root") },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text(stringResource(R.string.tab_explore)) }
                )
                NavigationBarItem(
                    selected = currentRoute == "watchlist_root",
                    onClick = { rootNav.navigate("watchlist_root") },
                    icon = { Icon(Icons.Default.Bookmark, null) },
                    label = { Text(stringResource(R.string.tab_watchlist)) }
                )
            }
        }
    ) { padding ->
        RootNavGraph(
            navController = rootNav,
            modifier = Modifier.padding(padding)
        )
    }
}
