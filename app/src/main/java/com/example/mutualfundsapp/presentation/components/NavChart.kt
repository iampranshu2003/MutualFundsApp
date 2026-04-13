package com.example.mutualfundsapp.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.example.mutualfundsapp.domain.model.NavPoint
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf

@Composable
fun NavChart(
    points: List<NavPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val entries = points.map { entryOf(it.nav) }
    val entryModel = entryModelOf(*entries.toTypedArray())

    ProvideChartStyle {
        Chart(
            chart = lineChart(),
            model = entryModel,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
            modifier = modifier
        )
    }
}
