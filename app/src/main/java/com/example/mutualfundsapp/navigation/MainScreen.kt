package com.example.mutualfundsapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mutualfundsapp.R

@Composable
fun MainScreen() {
    val rootNav = rememberNavController()
    val currentRoute by rootNav.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute?.destination?.route == "explore_root",
                    onClick = { rootNav.navigate("explore_root") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_explore)) }
                )
                NavigationBarItem(
                    selected = currentRoute?.destination?.route == "watchlist_root",
                    onClick = { rootNav.navigate("watchlist_root") },
                    icon = { Icon(painterResource(R.drawable.bookmark), contentDescription = null) },
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
