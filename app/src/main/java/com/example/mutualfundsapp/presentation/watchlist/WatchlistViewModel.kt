package com.example.mutualfundsapp.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mutualfundsapp.domain.usecase.CreateWatchlistUseCase
import com.example.mutualfundsapp.domain.usecase.GetAllWatchlistsUseCase
import com.example.mutualfundsapp.domain.usecase.RemoveFundFromWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val getAllWatchlistsUseCase: GetAllWatchlistsUseCase,
    private val createWatchlistUseCase: CreateWatchlistUseCase,
    private val removeFundFromWatchlistUseCase: RemoveFundFromWatchlistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistState(isLoading = true))
    val uiState: StateFlow<WatchlistState> = _uiState.asStateFlow()

    val effect = Channel<WatchlistEffect>(Channel.BUFFERED)

    init {
        loadWatchlists()
    }

    fun onEvent(event: WatchlistEvent) {
        when (event) {
            is WatchlistEvent.LoadWatchlists -> loadWatchlists()
            is WatchlistEvent.Retry -> loadWatchlists()
            is WatchlistEvent.OpenWatchlist -> openWatchlist(event.watchlistId)
            is WatchlistEvent.CreateWatchlist -> createWatchlist(event.name)
            is WatchlistEvent.RemoveFund -> removeFund(event.watchlistId, event.schemeCode)
            is WatchlistEvent.LoadWatchlistFunds -> loadWatchlistFunds(event.watchlistId)
            WatchlistEvent.NavigateToExplore -> navigateToExplore()
        }
    }

    private fun loadWatchlists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getAllWatchlistsUseCase().collectLatest { watchlists ->
                _uiState.update { it.copy(isLoading = false, watchlists = watchlists) }
            }
        }
    }

    private fun openWatchlist(watchlistId: String) {
        viewModelScope.launch { effect.send(WatchlistEffect.NavigateToFolder(watchlistId)) }
    }

    private fun createWatchlist(name: String) {
        viewModelScope.launch {
            val result = createWatchlistUseCase(name)
            if (result.isFailure) {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message.orEmpty()) }
            }
        }
    }

    private fun removeFund(watchlistId: String, schemeCode: String) {
        viewModelScope.launch {
            val result = removeFundFromWatchlistUseCase(watchlistId, schemeCode)
            if (result.isFailure) {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message.orEmpty()) }
            }
            loadWatchlistFunds(watchlistId)
        }
    }

    private fun loadWatchlistFunds(watchlistId: String) {
        val watchlist = _uiState.value.watchlists.find { it.id == watchlistId }
        val funds = watchlist?.funds ?: emptyList()
        _uiState.update {
            it.copy(
                isLoading = false,
                selectedWatchlistFunds = funds
            )
        }
    }

    private fun navigateToExplore() {
        viewModelScope.launch { effect.send(WatchlistEffect.NavigateToExplore) }
    }
}
