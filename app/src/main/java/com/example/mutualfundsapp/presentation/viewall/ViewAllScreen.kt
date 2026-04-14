package com.example.mutualfundsapp.presentation.viewall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mutualfundsapp.R
import com.example.mutualfundsapp.presentation.components.ErrorState
import com.example.mutualfundsapp.presentation.components.FundListItem
import com.example.mutualfundsapp.presentation.components.LoadingState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllRoute(
    category: String,
    onNavigateBack: () -> Unit,
    onNavigateToFundDetail: (String) -> Unit,
    viewModel: ViewAllViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.receiveAsFlow().collectLatest { effect ->
            when (effect) {
                is ViewAllEffect.NavigateToFundDetail -> onNavigateToFundDetail(effect.schemeCode)
            }
        }
    }

    ViewAllScreen(
        state = state,
        category = category,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllScreen(
    state: ViewAllState,
    category: String,
    onEvent: (ViewAllEvent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Load initial data when category changes
    LaunchedEffect(category) {
        onEvent(ViewAllEvent.Load(category))
    }

    // Infinite scroll: trigger next page when nearing end
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= state.visibleFunds.size - 5) {
                    onEvent(ViewAllEvent.LoadNextPage)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = categoryTitle(category)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                state.isLoading -> LoadingState()
                state.error != null -> {
                    val message = state.error?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.unknown_error)
                    ErrorState(
                        message = message,
                        onRetry = { onEvent(ViewAllEvent.Retry) }
                    )
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.visibleFunds,
                            key = { it.schemeCode }
                        ) { fund ->
                            FundListItem(
                                name = fund.schemeName,
                                nav = fund.nav,
                                onClick = { onEvent(ViewAllEvent.OpenFund(fund.schemeCode)) }
                            )
                        }

                        // Footer: loading indicator or end-of-list message
                        item {
                            when {
                                state.isLoadingMore -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                !state.hasMore && state.visibleFunds.isNotEmpty() -> {
                                    Text(
                                        text = stringResource(R.string.no_more_funds),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun categoryTitle(category: String): String {
    return when (category) {
        "all" -> stringResource(R.string.category_all)
        "index" -> stringResource(R.string.category_index)
        "bluechip" -> stringResource(R.string.category_bluechip)
        "tax" -> stringResource(R.string.category_tax)
        "largecap" -> stringResource(R.string.category_largecap)
        else -> category
    }
}
