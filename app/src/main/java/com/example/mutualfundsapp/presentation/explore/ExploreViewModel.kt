package com.example.mutualfundsapp.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mutualfundsapp.data.cache.ExploreCacheDataStore
import com.example.mutualfundsapp.domain.usecase.GetExploreCategoriesUseCase
import com.example.mutualfundsapp.presentation.explore.ExploreEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getExploreCategoriesUseCase: GetExploreCategoriesUseCase,
    private val cacheDataStore: ExploreCacheDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreState(isLoading = true))
    val uiState: StateFlow<ExploreState> = _uiState

    val effect = Channel<ExploreEffect>(Channel.BUFFERED)

    init {
        loadFromCacheThenRefresh()
    }

    fun onEvent(event: ExploreEvent) {
        when (event) {
            ExploreEvent.LoadExplore,
            ExploreEvent.Retry -> loadFromCacheThenRefresh()
            is ExploreEvent.OpenFund -> sendEffect(NavigateToFundDetail(event.schemeCode))
            ExploreEvent.OpenSearch -> sendEffect(ExploreEffect.NavigateToSearch)
            is ExploreEvent.OpenCategory -> sendEffect(NavigateToViewAll(event.category))
        }
    }

    private fun loadFromCacheThenRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val cache = cacheDataStore.observeCache().first()
            if (cache != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        categories = cache.categories
                    )
                }
            }
            val result = getExploreCategoriesUseCase()
            result.fold(
                onSuccess = { categories ->
                    _uiState.update {
                        it.copy(isLoading = false, error = null, categories = categories)
                    }
                },
                onFailure = { err ->
                    val hasCache = _uiState.value.categories.isNotEmpty()
                    if (!hasCache) {
                        _uiState.update {
                            it.copy(isLoading = false, error = err.message ?: "Unknown error")
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = null) }
                    }
                }
            )
        }
    }

    private fun sendEffect(effectValue: ExploreEffect) {
        viewModelScope.launch {
            effect.send(effectValue)
        }
    }
}
