package com.jurjandreigeorge.defroster.presentation.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jurjandreigeorge.defroster.data.HeatingStatsEntity
import com.jurjandreigeorge.defroster.presentation.theme.getChartColorFill
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun HeatingTimeSeriesChart(
    heatingStats: HeatingStatsEntity
) {
    val timeSeriesTemps = remember {
        heatingStats.timeSeriesTemps
            .split(",")
            .map { it.trim().toFloat() }
    }
    val timeSeriesTimestamps = remember {
        heatingStats.timeSeriesTimestamps
            .split(",")
            .map { it.trim() }
    }

    val minYRange = remember { floor(heatingStats.minTemp).toDouble() }
    val maxYRange = remember { ceil(heatingStats.maxTemp).toDouble() }
    val rangeProvider = remember { CartesianLayerRangeProvider.fixed(
        minX = 0.toDouble(),
        maxX = (timeSeriesTimestamps.size - 1).toDouble(),
        minY = minYRange,
        maxY = maxYRange
    ) }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries {
                series(y = timeSeriesTemps)
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(
                            getChartColorFill(
                                minY = minYRange,
                                maxY = maxYRange
                            )
                        ),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            getChartColorFill(
                                minY = minYRange,
                                maxY = maxYRange,
                                alpha = 0.25f
                            )
                        )
                    )
                ),
                rangeProvider = rangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(
                title = "Temperature (°C)",
                titleComponent = rememberAxisLabelComponent(
                    textSize = 16.sp
                ),
                label = rememberAxisLabelComponent(
                    textSize = 16.sp
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                title = "Time since start",
                titleComponent = rememberAxisLabelComponent(
                    textSize = 16.sp
                ),
                label = rememberAxisLabelComponent(
                    textSize = 16.sp
                ),
                valueFormatter = { _, value, _ ->
                    val currentPointSecondsElapsed = value.toInt() * heatingStats.timeSeriesSamplingPeriodSeconds
                    val hours = currentPointSecondsElapsed / 3600
                    val minutes = (currentPointSecondsElapsed % 3600) / 60
                    val seconds = currentPointSecondsElapsed % 60
                    buildString {
                        append("+")
                        if (hours > 0) append("${hours}h")
                        if (minutes > 0) append("${minutes}m")
                        if (seconds > 0 || (seconds == 0L && minutes == 0L && hours == 0L)) append("${seconds}s")
                    }
                }
            ),
            marker = rememberHeatingTimeSeriesChartMarker(
                valueFormatter = { _, targets ->
                    val currentIdx = targets.first().x.toInt()
                    "${timeSeriesTimestamps[currentIdx]}: ${"%.2f".format(timeSeriesTemps[currentIdx])} °C"
                }
            ),
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(),
        zoomState = rememberVicoZoomState(),
        modifier = Modifier.fillMaxWidth().height(350.dp)
    )
}
