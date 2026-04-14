package com.example.mutualfundsapp.presentation.explore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    val themeViewModel: ThemeViewModel = hiltViewModel()

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
