package com.example.mutualfundsapp.presentation.explore

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mutualfundsapp.ui.theme.ThemeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun ExploreRoute(
    onNavigateToSearch: () -> Unit,
    onNavigateToViewAll: (String) -> Unit,
    onNavigateToFundDetail: (String) -> Unit
) {
    val viewModel: ExploreViewModel = hiltViewModel()
    // Must use Activity scope: nested composables default to NavBackStackEntry, which would
    // create a second ThemeViewModel — MainActivity would never see toggle updates.
    val activity = LocalContext.current as ComponentActivity
    val themeViewModel: ThemeViewModel = hiltViewModel(activity)

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ExploreScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onToggleTheme = { themeViewModel.toggleTheme() }
    )

    LaunchedEffect(Unit) {
        viewModel.effect.receiveAsFlow().collectLatest { effect ->
            when (effect) {
                is ExploreEffect.NavigateToFundDetail -> onNavigateToFundDetail(effect.schemeCode)
                ExploreEffect.NavigateToSearch -> onNavigateToSearch()
                is ExploreEffect.NavigateToViewAll -> onNavigateToViewAll(effect.category)
            }
        }
    }
}
