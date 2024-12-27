package com.jurjandreigeorge.defroster.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import com.jurjandreigeorge.defroster.domain.HeatingState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import kotlin.ranges.coerceIn

val coldColor = Color(0xFF257ca3)
val hotColor = Color(0xFFDC143C)

const val COLD_COLOR_GRADIENT_THRESHOLD = 30f
const val HOT_COLOR_GRADIENT_THRESHOLD = 70f

private fun Int.mapToRange(min: Float, max: Float): Float {
    return (this - min) / (max - min)
}
fun getTempColor(temp: Int): Color {
    return lerp(coldColor, hotColor, temp.mapToRange(COLD_COLOR_GRADIENT_THRESHOLD, HOT_COLOR_GRADIENT_THRESHOLD))
}

private fun Float.mapToRange(min: Float, max: Float): Float {
    return (this - min) / (max - min)
}
fun getTempColor(temp: Float): Color {
    return lerp(coldColor, hotColor, temp.mapToRange(COLD_COLOR_GRADIENT_THRESHOLD, HOT_COLOR_GRADIENT_THRESHOLD))
}

private fun Double.mapToRange(min: Float, max: Float): Float {
    return (this.toFloat() - min) / (max - min)
}
fun getTempColor(temp: Double): Color {
    return lerp(coldColor, hotColor, temp.mapToRange(COLD_COLOR_GRADIENT_THRESHOLD, HOT_COLOR_GRADIENT_THRESHOLD))
}

fun getDisabledTempColor(tempColor: Color, heatingState: HeatingState): Color {
    return lerp(tempColor, backgroundColors[heatingState]!!, 0.5f)
}

val backgroundColors = mapOf(
    HeatingState.STARTING_HEATING to Color(0xFFA2F2F0),
    HeatingState.HEATING to Color(0xFFFFB3B3),
    HeatingState.STOPPING_HEATING to Color(0xFFFFB3B3),
    HeatingState.NOT_HEATING to Color(0xFFA2F2F0)
)

@Composable
fun getBackgroundColorGradient(heatingState: HeatingState): Brush {
    return Brush.radialGradient(
        colors = listOf(
            Color.White,
            backgroundColors[heatingState]!!
        ),
        radius = maxOf(
            LocalConfiguration.current.screenWidthDp,
            LocalConfiguration.current.screenHeightDp
        ).toFloat() * 3f
    )
}

@Composable
fun getChartColorFill(minY: Double, maxY: Double, alpha: Float = 1f): Fill {
    var gradientHotColor: Color = getTempColor(maxY)
    var gradientColdColor: Color = getTempColor(minY)
    if (alpha != 1f)  {
        gradientHotColor = gradientHotColor.copy(alpha = alpha)
        gradientColdColor = gradientColdColor.copy(alpha = alpha)
    }
    val colorSteps = mutableListOf<Pair<Color, Float>>()
    return when {
        // Full cold color when max is below threshold
        maxY <= COLD_COLOR_GRADIENT_THRESHOLD -> fill(gradientColdColor)
        // Full hot color when min is above threshold
        minY >= HOT_COLOR_GRADIENT_THRESHOLD -> fill(gradientHotColor)
        else -> {
            // Get gradient positions
            val gradientColdSidePosition = ((COLD_COLOR_GRADIENT_THRESHOLD - minY) / (maxY - minY)).coerceIn(0.0, 1.0).toFloat()
            val gradientHotSidePosition = ((HOT_COLOR_GRADIENT_THRESHOLD - minY) / (maxY - minY)).coerceIn(0.0, 1.0).toFloat()
            // Add positions as required
            colorSteps.add(Pair(gradientHotColor, 0f))
            if (minY < HOT_COLOR_GRADIENT_THRESHOLD && maxY > HOT_COLOR_GRADIENT_THRESHOLD) {
                colorSteps.add(Pair(gradientHotColor, 1f - gradientHotSidePosition))
            }
            if (minY < COLD_COLOR_GRADIENT_THRESHOLD && maxY > COLD_COLOR_GRADIENT_THRESHOLD) {
                colorSteps.add(Pair(gradientColdColor, 1f - gradientColdSidePosition))
            }
            colorSteps.add(Pair(gradientColdColor, 1f))
            // Create fill
            return fill(DynamicShader.verticalGradient(
                colors = colorSteps.map { it.first.toArgb() }.toIntArray(),
                positions = colorSteps.map { it.second }.toFloatArray()
            ))
        }
    }
}
