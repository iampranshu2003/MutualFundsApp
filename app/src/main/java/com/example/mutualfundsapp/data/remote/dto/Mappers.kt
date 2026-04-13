package com.example.mutualfundsapp.data.remote.dto

import com.example.mutualfundsapp.domain.model.FundDetail
import com.example.mutualfundsapp.domain.model.FundSummary
import com.example.mutualfundsapp.domain.model.NavPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val NAV_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")

fun SearchResultDto.toDomain(nav: String = "--"): FundSummary {
    return FundSummary(
        schemeCode = schemeCode,
        schemeName = schemeName,
        nav = nav
    )
}

fun FundDetailDto.toDomain(): FundDetail {
    val navHistory = data.mapNotNull { entry ->
        runCatching {
            NavPoint(
                date = LocalDate.parse(entry.date, NAV_DATE_FORMAT),
                nav = entry.nav.toFloat()
            )
        }.getOrNull()
    }

    val latestNav = data.firstOrNull()?.nav ?: "--"

    return FundDetail(
        amcName = meta.fund_house,
        schemeType = meta.scheme_type,
        nav = latestNav,
        navHistory = navHistory
    )
}
