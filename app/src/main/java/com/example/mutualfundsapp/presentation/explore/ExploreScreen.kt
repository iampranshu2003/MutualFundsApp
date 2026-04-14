package com.example.mutualfundsapp.presentation.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
            title = { Text("Mf Explorer") },
            actions = {
                Text(
                    text = stringResource(R.string.view_all),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { onEvent(ExploreEvent.OpenCategory("all")) }
                )
                IconButton(onClick = onToggleTheme) {
                    Icon(painterResource(R.drawable.dark), contentDescription = stringResource(R.string.toggle_theme))
                }
            }
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onEvent(ExploreEvent.OpenSearch) },
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
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
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        sections.forEach { (key, title) ->
                            val funds = state.categories[key].orEmpty().take(4)
                            if (funds.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = title.uppercase(), style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        text = stringResource(R.string.view_all),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .clickable { onEvent(ExploreEvent.OpenCategory(key)) }
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    funds.chunked(2).forEach { rowFunds ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            rowFunds.forEach { fund ->
                                                FundCard(
                                                    name = fund.schemeName,
                                                    nav = fund.nav,
                                                    onClick = { onEvent(ExploreEvent.OpenFund(fund.schemeCode)) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            if (rowFunds.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
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
    }
}
