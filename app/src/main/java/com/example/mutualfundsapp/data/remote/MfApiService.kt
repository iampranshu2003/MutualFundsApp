package com.example.mutualfundsapp.data.remote

import com.example.mutualfundsapp.data.remote.dto.FundDetailDto
import com.example.mutualfundsapp.data.remote.dto.SearchResultDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MfApiService {

    @GET("mf")
    suspend fun getAllFunds(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): List<SearchResultDto>

    @GET("mf/search")
    suspend fun searchFunds(@Query("q") query: String): List<SearchResultDto>

    @GET("mf/{scheme_code}")
    suspend fun getFundDetail(@Path("scheme_code") schemeCode: String): FundDetailDto

    @GET("mf/{scheme_code}/latest")
    suspend fun getLatestNav(@Path("scheme_code") schemeCode: String): FundDetailDto
}