package com.example.mutualfundsapp.presentation.viewall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mutualfundsapp.domain.usecase.SearchFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewAllViewModel @Inject constructor(
    private val searchFundsUseCase: SearchFundsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViewAllState())
    val uiState: StateFlow<ViewAllState> = _uiState.asStateFlow()

    val effect = Channel<ViewAllEffect>(Channel.BUFFERED)

    fun onEvent(event: ViewAllEvent) {
        when (event) {
            is ViewAllEvent.Load -> load(event.category)
            ViewAllEvent.LoadNextPage -> loadNextPage()
            ViewAllEvent.Retry -> retry()
            is ViewAllEvent.OpenFund -> openFund(event.schemeCode)
        }
    }

    private fun load(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, category = category) }

            val result = searchFundsUseCase(category)
            result.fold(
                onSuccess = { funds ->
                    val initial = funds.take(_uiState.value.pageSize)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            allFunds = funds,
                            visibleFunds = initial,
                            loadedCount = initial.size,
                            hasMore = funds.size > initial.size
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message.orEmpty()
                        )
                    }
                }
            )
        }
    }

    private fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val nextCount = (currentState.loadedCount + currentState.pageSize)
                .coerceAtMost(currentState.allFunds.size)
            val nextPage = currentState.allFunds.take(nextCount)

            _uiState.update {
                it.copy(
                    isLoadingMore = false,
                    visibleFunds = nextPage,
                    loadedCount = nextCount,
                    hasMore = nextCount < currentState.allFunds.size
                )
            }
        }
    }

    private fun retry() {
        val currentCategory = _uiState.value.category
        if (currentCategory.isNotBlank()) {
            load(currentCategory)
        }
    }

    private fun openFund(schemeCode: String) {
        viewModelScope.launch {
            effect.send(ViewAllEffect.NavigateToFundDetail(schemeCode))
        }
    }
}
