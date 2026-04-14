package com.example.mutualfundsapp

import app.cash.turbine.test
import com.example.mutualfundsapp.domain.model.FundSummary
import com.example.mutualfundsapp.domain.usecase.SearchFundsUseCase
import com.example.mutualfundsapp.presentation.search.SearchEvent
import com.example.mutualfundsapp.presentation.search.SearchViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val useCase: SearchFundsUseCase = mockk()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = SearchViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `typing less than 2 chars does not call api`() = runTest {
        viewModel.onEvent(SearchEvent.UpdateQuery("a"))
        advanceTimeBy(400)
        coVerify(exactly = 0) { useCase(any()) }
    }

    @Test
    fun `typing 2 or more chars triggers loading then success`() = runTest {
        val funds = listOf(FundSummary("1", "Fund", "10.0"))
        coEvery { useCase("ab") } returns Result.success(funds)

        viewModel.onEvent(SearchEvent.UpdateQuery("ab"))
        advanceTimeBy(301)
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.results.size)
    }

    @Test
    fun `rapid typing within debounce triggers only one api call`() = runTest {
        coEvery { useCase(any()) } returns Result.success(emptyList())

        viewModel.onEvent(SearchEvent.UpdateQuery("ab"))
        viewModel.onEvent(SearchEvent.UpdateQuery("abc"))
        viewModel.onEvent(SearchEvent.UpdateQuery("abcd"))

        advanceTimeBy(301)
        runCurrent()

        coVerify(exactly = 1) { useCase("abcd") }
    }
}
