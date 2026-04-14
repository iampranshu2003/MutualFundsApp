package com.example.mutualfundsapp

import com.example.mutualfundsapp.data.local.dao.WatchlistDao
import com.example.mutualfundsapp.data.local.dao.WatchlistFundDao
import com.example.mutualfundsapp.data.repository.WatchlistRepositoryImpl
import com.example.mutualfundsapp.domain.model.FundSummary
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistRepositoryImplTest {

    private val watchlistDao: WatchlistDao = mockk()
    private val watchlistFundDao: WatchlistFundDao = mockk()
    private lateinit var repo: WatchlistRepositoryImpl

    @Before
    fun setup() {
        repo = WatchlistRepositoryImpl(watchlistDao, watchlistFundDao)
    }

    @Test
    fun `adding fund inserts correct entity`() = runTest {
        coEvery { watchlistFundDao.insertFund(any()) } just Runs

        val fund = FundSummary("1", "Fund A", "10.0")
        repo.addFundToWatchlist("watch1", fund)

        coVerify {
            watchlistFundDao.insertFund(
                withArg {
                    assertTrue(it.watchlistId == "watch1")
                    assertTrue(it.schemeCode == "1")
                    assertTrue(it.schemeName == "Fund A")
                }
            )
        }
    }

    @Test
    fun `removing fund deletes only that fund`() = runTest {
        coEvery { watchlistFundDao.deleteFund(any(), any()) } just Runs

        repo.removeFundFromWatchlist("watch1", "1")

        coVerify(exactly = 1) { watchlistFundDao.deleteFund("watch1", "1") }
    }

    @Test
    fun `isFundInAnyWatchlist returns true if exists`() = runTest {
        coEvery { watchlistFundDao.isFundInAnyWatchlist("1") } returns flowOf(true)

        val result = repo.isFundInAnyWatchlist("1").first()
        assertTrue(result)
    }
}
