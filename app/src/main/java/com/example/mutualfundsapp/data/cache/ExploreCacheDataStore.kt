package com.example.mutualfundsapp.data.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mutualfundsapp.domain.model.FundSummary
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreCacheDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) {
    private val key = stringPreferencesKey("explore_cache")

    fun observeCache(): Flow<ExploreCache?> {
        return dataStore.data.map { prefs ->
            prefs[key]?.let { json ->
                runCatching {
                    gson.fromJson<ExploreCache>(json, object : TypeToken<ExploreCache>() {}.type)
                }.getOrNull()
            }
        }
    }

    suspend fun saveCache(cache: ExploreCache) {
        dataStore.edit { prefs ->
            prefs[key] = gson.toJson(cache)
        }
    }

    data class ExploreCache(
        val timestamp: Long,
        val categories: Map<String, List<FundSummary>>
    )
}