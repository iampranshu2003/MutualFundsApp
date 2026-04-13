package com.example.mutualfundsapp.data.remote.dto

data class FundDetailDto(
    val meta: FundMetaDto,
    val data: List<NavEntryDto>
)

data class FundMetaDto(
    val fund_house: String,
    val scheme_type: String,
    val scheme_name: String
)

data class NavEntryDto(
    val date: String,
    val nav: String
)