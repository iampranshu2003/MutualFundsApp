package com.example.mutualfundsapp.presentation.watchlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mutualfundsapp.R
import com.example.mutualfundsapp.domain.model.FundSummary
import com.example.mutualfundsapp.presentation.components.EmptyState
import com.example.mutualfundsapp.presentation.components.ErrorState
import com.example.mutualfundsapp.presentation.components.FundListItem
import com.example.mutualfundsapp.presentation.components.LoadingState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun WatchlistFolderRoute(
    watchlistId: String,
    onNavigateToExplore: () -> Unit,
    onNavigateToFundDetail: (String) -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    WatchlistFolderScreen(
        state = state,
        watchlistId = watchlistId,
        onEvent = viewModel::onEvent,
        onFundClick = onNavigateToFundDetail,
        modifier = modifier
    )

    LaunchedEffect(Unit) {
        viewModel.effect.receiveAsFlow().collectLatest { effect ->
            when (effect) {
                is WatchlistEffect.NavigateToFolder -> {}
                WatchlistEffect.NavigateToExplore -> onNavigateToExplore()
            }
        }
    }
}

@Composable
fun WatchlistFolderScreen(
    state: WatchlistState,
    watchlistId: String,
    onEvent: (WatchlistEvent) -> Unit,
    onFundClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(watchlistId) {
        onEvent(WatchlistEvent.LoadWatchlistFunds(watchlistId))
    }

    when {
        state.isLoading -> LoadingState(modifier)
        state.error != null -> {
            val message = state.error?.takeIf { it.isNotBlank() }
                ?: stringResource(R.string.unknown_error)
            ErrorState(
                message = message,
                onRetry = { onEvent(WatchlistEvent.Retry) },
                modifier = modifier
            )
        }
        state.selectedWatchlistFunds.isEmpty() -> EmptyState(
            title = stringResource(R.string.watchlist_folder_empty_title),
            subtitle = stringResource(R.string.watchlist_folder_empty_subtitle),
            buttonText = stringResource(R.string.explore_funds),
            onButtonClick = { onEvent(WatchlistEvent.NavigateToExplore) },
            modifier = modifier
        )
        else -> FundListContent(
            funds = state.selectedWatchlistFunds,
            watchlistId = watchlistId,
            onFundClick = onFundClick,
            onRemoveFund = { schemeCode -> onEvent(WatchlistEvent.RemoveFund(watchlistId, schemeCode)) },
            modifier = modifier
        )
    }
}

@Composable
private fun FundListContent(
    funds: List<FundSummary>,
    watchlistId: String,
    onFundClick: (String) -> Unit,
    onRemoveFund: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(funds, key = { it.schemeCode }) { fund ->
            FundListItem(
                name = fund.schemeName,
                nav = fund.nav,
                onClick = { onFundClick(fund.schemeCode) },
                trailing = {
                    IconButton(
                        onClick = { onRemoveFund(fund.schemeCode) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.remove_fund)
                        )
                    }
                }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}
