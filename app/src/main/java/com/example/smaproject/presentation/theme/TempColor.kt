package com.example.smaproject.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import com.example.smaproject.domain.HeatingState

val coldColor = Color(0xFF257ca3)
val hotColor = Color(0xFFDC143C)

private fun Int.mapToRange(min: Float, max: Float): Float {
    return (this - min) / (max - min)
}
fun getTempColor(temp: Int): Color {
    return lerp(coldColor, hotColor, temp.mapToRange(30f, 70f))
}

private fun Float.mapToRange(min: Float, max: Float): Float {
    return (this - min) / (max - min)
}
fun getTempColor(temp: Float): Color {
    return lerp(coldColor, hotColor, temp.mapToRange(30f, 70f))
}

fun getDisabledTempColor(tempColor: Color, heatingState: HeatingState): Color {
    return lerp(tempColor, backgroundColors[heatingState]!!, 0.5f)
}

val backgroundColors = mapOf(
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
