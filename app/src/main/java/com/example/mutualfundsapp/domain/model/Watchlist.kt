package com.example.mutualfundsapp.domain.model

data class Watchlist(
    val id: String,
    val name: String,
    val funds: List<FundSummary>
)