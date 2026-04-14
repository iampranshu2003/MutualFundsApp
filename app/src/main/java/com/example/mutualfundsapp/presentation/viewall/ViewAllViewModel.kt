package com.example.mutualfundsapp.presentation.viewall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mutualfundsapp.domain.model.FundSummary
import com.example.mutualfundsapp.domain.usecase.GetAllFundsUseCase
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
    private val searchFundsUseCase: SearchFundsUseCase,
    private val getAllFundsUseCase: GetAllFundsUseCase
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
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    error = null,
                    category = category,
                    allFunds = emptyList(),
                    visibleFunds = emptyList(),
                    loadedCount = 0,
                    hasMore = true
                )
            }

            val result = if (category == "all") {
                getAllFundsUseCase(limit = _uiState.value.pageSize, offset = 0)
            } else {
                searchFundsUseCase(category)
            }
            result.fold(
                onSuccess = { funds ->
                    val visible = if (category == "all") {
                        funds
                    } else {
                        funds.take(_uiState.value.pageSize)
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            allFunds = funds,
                            visibleFunds = visible,
                            loadedCount = visible.size,
                            hasMore = if (category == "all") {
                                funds.size == _uiState.value.pageSize
                            } else {
                                funds.size > visible.size
                            }
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

        if (currentState.category == "all") {
            loadNextAllFundsPage(currentState)
            return
        }

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

    private fun loadNextAllFundsPage(currentState: ViewAllState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val result = getAllFundsUseCase(
                limit = currentState.pageSize,
                offset = currentState.loadedCount
            )

            result.fold(
                onSuccess = { funds ->
                    val merged = (currentState.allFunds + funds).distinctBy { it.schemeCode }
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            allFunds = merged,
                            visibleFunds = merged,
                            loadedCount = currentState.loadedCount + funds.size,
                            hasMore = funds.size == currentState.pageSize
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            error = throwable.message.orEmpty()
                        )
                    }
                }
            )
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
