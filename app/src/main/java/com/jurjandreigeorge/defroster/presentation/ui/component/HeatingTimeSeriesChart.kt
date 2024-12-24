package com.jurjandreigeorge.defroster.presentation.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.jurjandreigeorge.defroster.data.HeatingStatsEntity


@Composable
fun HeatingTimeSeriesChart(
    heatingStats: HeatingStatsEntity,
    textColor: Color,
    cardContainerColor: Color
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

    val dataPoints: List<Point> = remember {
        timeSeriesTemps.mapIndexed { index, temp ->
            Point(index.toFloat(), temp)
        }
    }

    val xAxisData = AxisData.Builder()
        .steps(dataPoints.size - 1)
        .axisStepSize(50.dp)
        .backgroundColor(cardContainerColor)
        .labelData { i ->
            val currentPointSecondsElapsed = i * heatingStats.timeSeriesSamplingPeriodSeconds
            val hours = currentPointSecondsElapsed / 3600
            val minutes = (currentPointSecondsElapsed % 3600) / 60
            val seconds = currentPointSecondsElapsed % 60

            val formattedDuration = buildString {
                append("+")
                if (hours > 0) append("${hours}h")
                if (minutes > 0) append("${minutes}m")
                if (seconds > 0 || (seconds == 0L && minutes == 0L && hours == 0L)) append("${seconds}s")
            }
            formattedDuration
        }
        .axisLabelAngle(17.5f)
        .labelAndAxisLinePadding(8.dp)
        .axisLineColor(textColor)
        .axisLabelColor(textColor)
        .setDataCategoryOptions(
            DataCategoryOptions(
                isDataCategoryInYAxis = false,
                isDataCategoryStartFromBottom = true
            )
        )
        .build()

    val yAxisSteps = 10
    val yAxisData = AxisData.Builder()
        .steps(yAxisSteps)
        .backgroundColor(cardContainerColor)
        .labelData { i ->
            val yScale = (heatingStats.maxTemp - heatingStats.minTemp) / yAxisSteps
            "%.2f".format((i * yScale) + heatingStats.minTemp)
        }
        .axisLineColor(textColor)
        .axisLabelColor(textColor)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = dataPoints,
                    lineStyle = LineStyle(
                        color = textColor,
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.inversePrimary,
                                Color.Transparent
                            )
                        )
                    ),
                    intersectionPoint = IntersectionPoint(
                        color = textColor,
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.primary,
                        radius = 8.dp
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = MaterialTheme.colorScheme.inverseSurface,
                        labelColor = MaterialTheme.colorScheme.inverseOnSurface,
                        popUpLabel = { x, y ->
                            "${timeSeriesTimestamps[x.toInt()]}: ${"%.2f".format(y)} °C"
                        }
                    )
                )
            )
        ),
        backgroundColor = cardContainerColor,
        containerPaddingEnd = 100.dp,
        paddingTop = 35.dp,
        paddingRight = 0.dp,
        bottomPadding = 16.dp,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(
            color = MaterialTheme.colorScheme.outlineVariant
        )
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}