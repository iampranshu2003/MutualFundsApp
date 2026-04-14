package com.example.mutualfundsapp.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mutualfundsapp.R
import com.example.mutualfundsapp.presentation.components.EmptyState
import com.example.mutualfundsapp.presentation.components.ErrorState
import com.example.mutualfundsapp.presentation.components.FundListItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoute(
    onNavigateBack: () -> Unit,
    onNavigateToFundDetail: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )

    LaunchedEffect(Unit) {
        viewModel.effect.receiveAsFlow().collectLatest { effect ->
            when (effect) {
                is SearchEffect.NavigateToFundDetail -> onNavigateToFundDetail(effect.schemeCode)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    onEvent: (SearchEvent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            OutlinedTextField(
                value = state.query,
                onValueChange = { onEvent(SearchEvent.UpdateQuery(it)) },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = { Text(stringResource(R.string.search_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                trailingIcon = {
                    if (state.query.isNotBlank()) {
                        IconButton(onClick = { onEvent(SearchEvent.UpdateQuery("")) }) {
                            Text(text = "x")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors()
            )
        }

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.query.isBlank() -> EmptyState(
                    title = stringResource(R.string.search_empty_title),
                    subtitle = stringResource(R.string.search_empty_subtitle)
                )
                state.error != null -> {
                    val message = state.error?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.unknown_error)
                    ErrorState(
                        message = message,
                        onRetry = { onEvent(SearchEvent.Retry) }
                    )
                }
                state.results.isEmpty() -> EmptyState(
                    title = stringResource(R.string.search_no_results, state.query),
                    subtitle = stringResource(R.string.search_try_another)
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.results, key = { it.schemeCode }) { fund ->
                        FundListItem(
                            name = fund.schemeName,
                            nav = fund.nav,
                            onClick = { onEvent(SearchEvent.OpenFund(fund.schemeCode)) }
                        )
                    }
                }
            }
        }
    }
}
