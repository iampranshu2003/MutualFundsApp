package com.example.mutualfundsapp

import com.example.mutualfundsapp.data.cache.ExploreCacheDataStore
import com.example.mutualfundsapp.data.remote.MfApiService
import com.example.mutualfundsapp.data.remote.dto.SearchResultDto
import com.example.mutualfundsapp.data.repository.MfRepositoryImpl
import com.example.mutualfundsapp.domain.model.FundSummary
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MfRepositoryImplTest {

    private val api: MfApiService = mockk()
    private val cache: ExploreCacheDataStore = mockk()
    private lateinit var repo: MfRepositoryImpl

    @Before
    fun setup() {
        repo = MfRepositoryImpl(api, cache)
    }

    @Test
    fun `when cache is fresh return cached data and do not call api`() = runTest {
        val cached = ExploreCacheDataStore.ExploreCache(
            timestamp = System.currentTimeMillis(),
            categories = mapOf(
                "index" to listOf(FundSummary("1", "Fund A", "10.0"))
            )
        )
        every { cache.observeCache() } returns flowOf(cached)

        val result = repo.getExploreCategories()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.get("index")?.size)
        coVerify(exactly = 0) { api.searchFunds(any()) }
    }

    @Test
    fun `when cache is stale call api update cache and return fresh data`() = runTest {
        val stale = ExploreCacheDataStore.ExploreCache(
            timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000),
            categories = mapOf("index" to listOf(FundSummary("old", "Old Fund", "9.0")))
        )
        every { cache.observeCache() } returns flowOf(stale)
        coEvery { api.searchFunds("index") } returns listOf(SearchResultDto("1", "Fund A"))
        coEvery { api.searchFunds("bluechip") } returns listOf(SearchResultDto("2", "Fund B"))
        coEvery { api.searchFunds("tax") } returns listOf(SearchResultDto("3", "Fund C"))
        coEvery { api.searchFunds("largecap") } returns listOf(SearchResultDto("4", "Fund D"))
        coEvery { cache.saveCache(any()) } returns Unit

        val result = repo.getExploreCategories()

        assertTrue(result.isSuccess)
        assertEquals(4, result.getOrNull()?.size)
        coVerify(exactly = 4) { api.searchFunds(any()) }
        coVerify(exactly = 1) { cache.saveCache(any()) }
    }

    @Test
    fun `when api fails and cache exists return cached data`() = runTest {
        val cached = ExploreCacheDataStore.ExploreCache(
            timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000),
            categories = mapOf("index" to listOf(FundSummary("1", "Fund A", "10.0")))
        )
        every { cache.observeCache() } returns flowOf(cached)
        coEvery { api.searchFunds(any()) } throws RuntimeException("API down")

        val result = repo.getExploreCategories()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.get("index")?.size)
        coVerify { api.searchFunds(any()) }
    }

    @Test
    fun `when api fails and no cache return error`() = runTest {
        every { cache.observeCache() } returns flowOf(null)
        coEvery { api.searchFunds(any()) } throws RuntimeException("API down")

        val result = repo.getExploreCategories()

        assertFalse(result.isSuccess)
    }
}
