package com.example.mutualfundsapp.presentation.watchlist


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mutualfundsapp.R
import com.example.mutualfundsapp.domain.model.Watchlist
import com.example.mutualfundsapp.presentation.components.EmptyState
import com.example.mutualfundsapp.presentation.components.ErrorState
import com.example.mutualfundsapp.presentation.components.LoadingState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun WatchlistRoute(
    onNavigateToFolder: (String) -> Unit,
    onNavigateToExplore: () -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    WatchlistScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )

    LaunchedEffect(Unit) {
        viewModel.effect.receiveAsFlow().collectLatest { effect ->
            when (effect) {
                is WatchlistEffect.NavigateToFolder -> onNavigateToFolder(effect.watchlistId)
                WatchlistEffect.NavigateToExplore -> onNavigateToExplore()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    state: WatchlistState,
    onEvent: (WatchlistEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Portfolios") }
            )
        },
        modifier = modifier
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                state.isLoading -> LoadingState(Modifier.fillMaxSize())
                state.error != null -> {
                    val message = state.error?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.unknown_error)
                    ErrorState(
                        message = message,
                        onRetry = { onEvent(WatchlistEvent.Retry) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                state.watchlists.isEmpty() -> EmptyState(
                    title = stringResource(R.string.watchlist_empty_title),
                    subtitle = stringResource(R.string.watchlist_empty_subtitle),
                    buttonText = stringResource(R.string.explore_funds),
                    onButtonClick = { onEvent(WatchlistEvent.NavigateToExplore) },
                    modifier = Modifier.fillMaxSize()
                )
                else -> WatchlistListContent(
                    watchlists = state.watchlists,
                    onWatchlistClick = { watchlistId -> onEvent(WatchlistEvent.OpenWatchlist(watchlistId)) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun WatchlistListContent(
    watchlists: List<Watchlist>,
    onWatchlistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(watchlists) { watchlist ->
            WatchlistFolderCard(
                watchlist = watchlist,
                onClick = { onWatchlistClick(watchlist.id) }
            )
        }
    }
}

@Composable
private fun WatchlistFolderCard(
    watchlist: Watchlist,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = watchlist.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.watchlist_fund_count, watchlist.funds.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
