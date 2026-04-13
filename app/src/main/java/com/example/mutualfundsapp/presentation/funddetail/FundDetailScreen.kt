package com.example.mutualfundsapp.presentation.funddetail

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mutualfundsapp.R
import com.example.mutualfundsapp.presentation.components.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundDetailRoute(
    schemeCode: String,
    onNavigateBack: () -> Unit,
    viewModel: FundDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FundDetailScreen(
        state = state,
        schemeCode = schemeCode,
        effect = viewModel.effect,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundDetailScreen(
    schemeCode: String,
    state: FundDetailState,
    effect: Channel<FundDetailEffect>,
    onEvent: (FundDetailEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state.schemeName.ifEmpty { schemeCode }) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(FundDetailEvent.ToggleBookmark) }
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (state.isInWatchlist) {
                                    R.drawable.bookmark
                                } else {
                                    R.drawable.bookmark_outline
                                }
                            ),
                            contentDescription = stringResource(R.string.bookmark)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                state.isLoading -> LoadingState()
                state.error != null -> {
                    val message = state.error?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.unknown_error)
                    ErrorState(
                        message = message,
                        onRetry = { onEvent(FundDetailEvent.Retry) }
                    )
                }
                else -> FundDetailContent(state = state, onEvent = onEvent)
            }
        }
    }

    if (state.showBottomSheet) {
        AddToWatchlistBottomSheet(
            watchlists = state.watchlists,
            checked = state.watchlistSelections,
            onToggle = { watchlistId, checked ->
                onEvent(FundDetailEvent.ToggleWatchlist(watchlistId, checked))
            },
            onCreateWatchlist = { name ->
                onEvent(FundDetailEvent.CreateWatchlist(name))
            },
            onDone = { onEvent(FundDetailEvent.ApplyWatchlistChanges) }
        )
    }

    LaunchedEffect(schemeCode) {
        onEvent(FundDetailEvent.Load(schemeCode))
    }

    LaunchedEffect(effect) {
        effect.receiveAsFlow().collectLatest { emitted ->
            when (emitted) {
                is FundDetailEffect.ShowMessage -> {
                    val message = emitted.formatArg?.let {
                        context.getString(emitted.messageRes, it)
                    } ?: context.getString(emitted.messageRes)
                    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
private fun FundDetailContent(
    state: FundDetailState,
    onEvent: (FundDetailEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header info
        item {
            FundHeaderInfo(state = state)
        }

        // Chart
        item {
            NavChart(
                points = state.filteredHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }

        // Range selector pills
        item {
            RangeSelector(
                selectedRange = state.selectedRange,
                onRangeSelected = { range -> onEvent(FundDetailEvent.SelectRange(range)) }
            )
        }

        // Additional info rows (Type, AUM, NAV)
        item {
            InfoRow(state = state)
        }

        // Placeholder for additional sections (e.g., performance stats, holdings)
    }
}

@Composable
private fun FundHeaderInfo(state: FundDetailState) {
    Column {
        Text(
            text = state.amcName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AssistChip(
                onClick = { /* no action */ },
                label = { Text(state.schemeType) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.nav_value, state.nav),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        val changeValue = remember(state.navChangeValue) {
            String.format("%.2f", state.navChangeValue)
        }
        val changePercent = remember(state.navChangePercent) {
            String.format("%.2f", state.navChangePercent)
        }
        Text(
            text = if (state.navChangePositive) {
                stringResource(R.string.nav_change_positive, changeValue, changePercent)
            } else {
                stringResource(R.string.nav_change_negative, changeValue, changePercent)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (state.navChangePositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun RangeSelector(
    selectedRange: NavRange,
    onRangeSelected: (NavRange) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavRange.values().forEach { range ->
            FilterChip(
                selected = range == selectedRange,
                onClick = { onRangeSelected(range) },
                label = {
                    Text(
                        text = when (range) {
                            NavRange.ONE_MONTH -> stringResource(R.string.range_1m)
                            NavRange.THREE_MONTHS -> stringResource(R.string.range_3m)
                            NavRange.SIX_MONTHS -> stringResource(R.string.range_6m)
                            NavRange.ONE_YEAR -> stringResource(R.string.range_1y)
                            NavRange.ALL -> stringResource(R.string.range_all)
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun InfoRow(state: FundDetailState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoItem(label = stringResource(R.string.label_type), value = state.schemeType)
            InfoItem(label = stringResource(R.string.label_aum), value = stringResource(R.string.placeholder_dash))
            InfoItem(label = stringResource(R.string.label_nav), value = stringResource(R.string.nav_value, state.nav))
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
