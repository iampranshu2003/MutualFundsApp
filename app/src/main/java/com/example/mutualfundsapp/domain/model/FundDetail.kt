package com.example.mutualfundsapp.domain.model

data class FundDetail(
    val amcName: String,
    val schemeType: String,
    val nav: String,
    val navHistory: List<NavPoint>
)