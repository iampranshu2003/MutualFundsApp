package com.example.mutualfundsapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mutualfundsapp.domain.usecase.SearchFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchFundsUseCase: SearchFundsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    val effect = Channel<SearchEffect>(Channel.BUFFERED)

    private val queryFlow = MutableStateFlow("")

    init {
        queryFlow
            .debounce(300L)
            .filter { it.length >= 2 }
            .distinctUntilChanged()
            .onEach { query -> runSearch(query) }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.UpdateQuery -> {
                _uiState.update { it.copy(query = event.query) }
                queryFlow.value = event.query
                if (event.query.length < 2) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = emptyList(),
                            error = null
                        )
                    }
                }
            }
            SearchEvent.Retry -> {
                val currentQuery = _uiState.value.query
                if (currentQuery.length >= 2) {
                    viewModelScope.launch { runSearch(currentQuery) }
                }
            }
            is SearchEvent.OpenFund -> {
                viewModelScope.launch {
                    effect.send(SearchEffect.NavigateToFundDetail(event.schemeCode))
                }
            }
        }
    }

    private suspend fun runSearch(query: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        val result = searchFundsUseCase(query)
        result.fold(
            onSuccess = { funds ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        results = funds,
                        error = null
                    )
                }
            },
            onFailure = { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        results = emptyList(),
                        error = throwable.message.orEmpty()
                    )
                }
            }
        )
    }
}
