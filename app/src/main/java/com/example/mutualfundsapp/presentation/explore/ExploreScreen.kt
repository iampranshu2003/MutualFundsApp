package com.example.mutualfundsapp.presentation.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mutualfundsapp.R
import com.example.mutualfundsapp.presentation.components.EmptyState
import com.example.mutualfundsapp.presentation.components.ErrorState
import com.example.mutualfundsapp.presentation.components.FundCard
import com.example.mutualfundsapp.presentation.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    state: ExploreState,
    onEvent: (ExploreEvent) -> Unit,
    modifier: Modifier = Modifier,
    onToggleTheme: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name)) },
            actions = {
                IconButton(onClick = { onEvent(ExploreEvent.OpenSearch) }) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
                IconButton(onClick = onToggleTheme) {
                    Icon(painterResource(R.drawable.dark), contentDescription = stringResource(R.string.toggle_theme))
                }
            }
        )

        when {
            state.isLoading -> LoadingState(modifier = Modifier.fillMaxSize())
            state.error != null -> {
                val message = state.error?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.unknown_error)
                ErrorState(
                    message = message,
                    onRetry = { onEvent(ExploreEvent.Retry) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            state.categories.isEmpty() -> EmptyState(
                title = stringResource(R.string.empty_explore_title),
                subtitle = stringResource(R.string.empty_explore_subtitle)
            )
            else -> {
                val sections = listOf(
                    "index" to stringResource(R.string.category_index),
                    "bluechip" to stringResource(R.string.category_bluechip),
                    "tax" to stringResource(R.string.category_tax),
                    "largecap" to stringResource(R.string.category_largecap)
                )
                val hasAnyFunds = sections.any { (key, _) -> state.categories[key].orEmpty().isNotEmpty() }

                if (!hasAnyFunds) {
                    EmptyState(
                        title = stringResource(R.string.empty_explore_title),
                        subtitle = stringResource(R.string.empty_explore_subtitle)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        sections.forEach { (key, title) ->
                            val funds = state.categories[key].orEmpty()
                            if (funds.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = stringResource(R.string.view_all),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clickable { onEvent(ExploreEvent.OpenCategory(key)) }
                                    )
                                }

                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(funds) { fund ->
                                        FundCard(
                                            name = fund.schemeName,
                                            nav = fund.nav,
                                            onClick = { onEvent(ExploreEvent.OpenFund(fund.schemeCode)) }
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
}
