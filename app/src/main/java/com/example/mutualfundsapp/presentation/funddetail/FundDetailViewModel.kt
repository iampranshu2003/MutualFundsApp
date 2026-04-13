package com.example.mutualfundsapp.presentation.funddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mutualfundsapp.R
import com.example.mutualfundsapp.domain.model.FundSummary
import com.example.mutualfundsapp.domain.model.NavPoint
import com.example.mutualfundsapp.domain.usecase.AddFundToWatchlistUseCase
import com.example.mutualfundsapp.domain.usecase.CreateWatchlistUseCase
import com.example.mutualfundsapp.domain.usecase.GetAllWatchlistsUseCase
import com.example.mutualfundsapp.domain.usecase.GetFundDetailUseCase
import com.example.mutualfundsapp.domain.usecase.IsFundInAnyWatchlistUseCase
import com.example.mutualfundsapp.domain.usecase.RemoveFundFromWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class FundDetailViewModel @Inject constructor(
    private val getFundDetailUseCase: GetFundDetailUseCase,
    private val getAllWatchlistsUseCase: GetAllWatchlistsUseCase,
    private val isFundInAnyWatchlistUseCase: IsFundInAnyWatchlistUseCase,
    private val addFundToWatchlistUseCase: AddFundToWatchlistUseCase,
    private val removeFundFromWatchlistUseCase: RemoveFundFromWatchlistUseCase,
    private val createWatchlistUseCase: CreateWatchlistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FundDetailState())
    val uiState: StateFlow<FundDetailState> = _uiState.asStateFlow()

    val effect = Channel<FundDetailEffect>(Channel.BUFFERED)
    private var inWatchlistJob: Job? = null

    init {
        getAllWatchlistsUseCase()
            .onEach { watchlists ->
                val schemeCode = _uiState.value.schemeCode
                val selections = if (schemeCode.isNotBlank()) {
                    watchlists.associate { w ->
                        w.id to w.funds.any { it.schemeCode == schemeCode }
                    }
                } else {
                    emptyMap()
                }
                _uiState.update {
                    it.copy(
                        watchlists = watchlists,
                        watchlistSelections = selections
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: FundDetailEvent) {
        when (event) {
            is FundDetailEvent.Load -> loadFundDetails(event.schemeCode)
            is FundDetailEvent.SelectRange -> updateRange(event.range)
            FundDetailEvent.ToggleBookmark -> toggleBookmark()
            FundDetailEvent.CloseBottomSheet -> closeBottomSheet()
            is FundDetailEvent.ToggleWatchlist -> toggleWatchlistSelection(event.watchlistId, event.checked)
            is FundDetailEvent.CreateWatchlist -> createWatchlist(event.name)
            FundDetailEvent.ApplyWatchlistChanges -> applyWatchlistChanges()
            FundDetailEvent.Retry -> retryLoad()
        }
    }

    private fun loadFundDetails(schemeCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, schemeCode = schemeCode) }

            inWatchlistJob?.cancel()
            inWatchlistJob = isFundInAnyWatchlistUseCase(schemeCode)
                .distinctUntilChanged()
                .onEach { inWatchlist ->
                    _uiState.update { it.copy(isInWatchlist = inWatchlist) }
                }
                .launchIn(viewModelScope)

            val result = getFundDetailUseCase(schemeCode)
            result.fold(
                onSuccess = { detail ->
                    val sortedHistory = detail.navHistory.sortedBy { it.date }
                    val latestNav = sortedHistory.lastOrNull()?.nav ?: 0f
                    val previousNav = sortedHistory.getOrNull(sortedHistory.size - 2)?.nav
                    val navChangeValue = if (previousNav != null) latestNav - previousNav else 0f
                    val navChangePercent = if (previousNav != null && previousNav != 0f) {
                        (navChangeValue / previousNav) * 100f
                    } else 0f

                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            schemeName = currentState.schemeName.ifBlank { schemeCode },
                            amcName = detail.amcName,
                            schemeType = detail.schemeType,
                            nav = detail.nav,
                            navChangeValue = navChangeValue,
                            navChangePercent = navChangePercent,
                            navChangePositive = navChangeValue >= 0f,
                            fullHistory = sortedHistory,
                            filteredHistory = downsampleHistory(sortedHistory, currentState.selectedRange)
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message.orEmpty()) }
                }
            )
        }
    }

    private fun retryLoad() {
        val currentScheme = _uiState.value.schemeCode
        if (currentScheme.isNotBlank()) {
            loadFundDetails(currentScheme)
        }
    }

    private fun updateRange(range: NavRange) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedRange = range,
                filteredHistory = downsampleHistory(currentState.fullHistory, range)
            )
        }
    }

    private fun downsampleHistory(fullHistory: List<NavPoint>, range: NavRange): List<NavPoint> {
        if (fullHistory.isEmpty()) return emptyList()
        val base = when (range) {
            NavRange.ALL -> fullHistory
            NavRange.ONE_YEAR -> fullHistory.takeLast(365)
            NavRange.SIX_MONTHS -> fullHistory.takeLast(180)
            NavRange.THREE_MONTHS -> fullHistory.takeLast(90)
            NavRange.ONE_MONTH -> fullHistory.takeLast(30)
        }
        return when (range) {
            NavRange.ALL -> base.filterIndexed { index, _ -> index % 7 == 0 }
            NavRange.ONE_YEAR -> base.filterIndexed { index, _ -> index % 3 == 0 }
            else -> base
        }
    }

    private fun toggleBookmark() {
        val schemeCode = _uiState.value.schemeCode
        if (schemeCode.isBlank()) return
        val selections = _uiState.value.watchlists.associate { w ->
            w.id to w.funds.any { it.schemeCode == schemeCode }
        }
        _uiState.update {
            it.copy(
                watchlistSelections = selections,
                showBottomSheet = true
            )
        }
    }

    private fun closeBottomSheet() {
        _uiState.update { it.copy(showBottomSheet = false) }
    }

    private fun toggleWatchlistSelection(watchlistId: String, checked: Boolean) {
        _uiState.update { currentState ->
            val newSelections = currentState.watchlistSelections.toMutableMap()
            newSelections[watchlistId] = checked
            currentState.copy(watchlistSelections = newSelections)
        }
    }

    private fun createWatchlist(name: String) {
        viewModelScope.launch {
            val result = createWatchlistUseCase(name)
            result.fold(
                onSuccess = {
                    effect.send(FundDetailEffect.ShowMessage(R.string.watchlist_created, name))
                },
                onFailure = {
                    effect.send(FundDetailEffect.ShowMessage(R.string.watchlist_create_failed))
                }
            )
        }
    }

    private fun applyWatchlistChanges() {
        viewModelScope.launch {
            val state = _uiState.value
            val schemeCode = state.schemeCode
            val currentSelections = state.watchlistSelections

            val currentFund = FundSummary(
                schemeCode = schemeCode,
                schemeName = state.schemeName.ifBlank { schemeCode },
                nav = state.nav
            )

            val toAdd = state.watchlists.filter { watchlist ->
                currentSelections[watchlist.id] == true &&
                    watchlist.funds.none { it.schemeCode == schemeCode }
            }

            val toRemove = state.watchlists.filter { watchlist ->
                currentSelections[watchlist.id] == false &&
                    watchlist.funds.any { it.schemeCode == schemeCode }
            }

            val addResults = toAdd.map { addFundToWatchlistUseCase(it.id, currentFund) }
            val removeResults = toRemove.map { removeFundFromWatchlistUseCase(it.id, schemeCode) }

            val anyFailure = (addResults + removeResults).any { it.isFailure }
            if (anyFailure) {
                effect.send(FundDetailEffect.ShowMessage(R.string.watchlist_update_failed))
            }

            _uiState.update { it.copy(showBottomSheet = false) }
        }
    }
}
