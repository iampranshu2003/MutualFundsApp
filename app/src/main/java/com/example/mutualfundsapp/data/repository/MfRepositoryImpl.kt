package com.example.mutualfundsapp.data.repository

import com.example.mutualfundsapp.data.cache.ExploreCacheDataStore
import com.example.mutualfundsapp.data.remote.MfApiService
import com.example.mutualfundsapp.data.remote.dto.toDomain
import com.example.mutualfundsapp.domain.model.FundDetail
import com.example.mutualfundsapp.domain.model.FundSummary
import com.example.mutualfundsapp.domain.model.NavPoint
import com.example.mutualfundsapp.domain.repository.MutualFundsRepository // change to your interface name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map
import kotlin.collections.take

@Singleton
class MfRepositoryImpl @Inject constructor(
    private val api: MfApiService,
    private val cacheDataStore: ExploreCacheDataStore
) : MutualFundsRepository {
    private val latestNavCache = mutableMapOf<String, CachedNav>()

    override suspend fun getAllFunds(limit: Int, offset: Int): Result<List<FundSummary>> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getAllFunds(limit = limit, offset = offset).map { it.toDomain() }
            }
        }

    override suspend fun searchFunds(query: String): Result<List<FundSummary>> = withContext(Dispatchers.IO) {
        runCatching {
            api.searchFunds(query).map { it.toDomain() }
        }
    }

    override suspend fun getFundDetail(schemeCode: String): Result<FundDetail> = withContext(Dispatchers.IO) {
        runCatching {
            api.getFundDetail(schemeCode).toDomain()
        }
    }

    override suspend fun getExploreCategories(): Result<Map<String, List<FundSummary>>> =
        withContext(Dispatchers.IO) {
            val cache = cacheDataStore.observeCache().first()
            val now = System.currentTimeMillis()
            val isFresh = cache != null && now - cache.timestamp < 60 * 60 * 1000
            if (isFresh && cache != null) {
                return@withContext Result.success(cache.categories)
            }

            try {
                val categories = fetchExploreFromApi()
                cacheDataStore.saveCache(
                    ExploreCacheDataStore.ExploreCache(
                        timestamp = now,
                        categories = categories
                    )
                )
                Result.success(categories)
            } catch (e: Exception) {
                if (cache != null) {
                    Result.success(cache.categories)
                } else {
                    Result.failure(e)
                }
            }
        }

    private suspend fun fetchExploreFromApi(): Map<String, List<FundSummary>> = coroutineScope {
        val keywords = mapOf(
            "index" to "index",
            "bluechip" to "bluechip",
            "tax" to "tax",
            "largecap" to "largecap"
        )

        val deferred = keywords.map { (key, query) ->
            async {
                val funds = api.searchFunds(query).take(4).map { result ->
                    val latestNav = fetchLatestNav(result.schemeCode.toString())
                    result.toDomain(nav = latestNav)
                }
                key to funds
            }
        }

        deferred.awaitAll().toMap()
    }

    private suspend fun fetchLatestNav(schemeCode: String): String {
        val now = System.currentTimeMillis()
        val cached = latestNavCache[schemeCode]
        if (cached != null && now - cached.timestamp < LATEST_NAV_CACHE_TTL_MS) {
            return cached.nav
        }

        val nav = runCatching {
            api.getLatestNav(schemeCode).data.firstOrNull()?.nav ?: "--"
        }.getOrElse { "--" }

        latestNavCache[schemeCode] = CachedNav(nav = nav, timestamp = now)
        return nav
    }

    fun downsampleNavHistory(
        full: List<NavPoint>,
        range: NavRange
    ): List<NavPoint> {
        return when (range) {
            NavRange.ALL -> full.filterIndexed { index, _ -> index % 7 == 0 }
            NavRange.ONE_YEAR -> full.takeLast(365).filterIndexed { index, _ -> index % 3 == 0 }
            NavRange.SIX_MONTHS -> full.takeLast(180)
            NavRange.THREE_MONTHS -> full.takeLast(90)
            NavRange.ONE_MONTH -> full.takeLast(30)
        }
    }

    enum class NavRange { ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR, ALL }

    private data class CachedNav(
        val nav: String,
        val timestamp: Long
    )

    private companion object {
        const val LATEST_NAV_CACHE_TTL_MS = 30 * 60 * 1000L
    }
}
