package com.example.smaproject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.roundToInt

@Composable
fun SliderScreen(
    navController: NavController,
    defrosterViewModel: DefrosterViewModel,
    backgroundColors: Map<HeatingState, Color>
) {
    val coldColor = remember { Color(0xFF257ca3) }
    val hotColor = remember { Color(0xFFDC143C) }

    fun Float.mapToRange(min: Float, max: Float): Float {
        return (this - min) / (max - min)
    }
    fun Int.mapToRange(min: Float, max: Float): Float {
        return (this - min) / (max - min)
    }

    val currentTempColor by remember { derivedStateOf {
        lerp(coldColor, hotColor, defrosterViewModel.currentTemp.mapToRange(30f, 70f))
    } }
    val targetTempColor by remember { derivedStateOf {
        lerp(coldColor, hotColor, defrosterViewModel.targetTemp.mapToRange(30f, 70f))
    } }
    val targetTempDisabledColor by remember { derivedStateOf {
        lerp(targetTempColor, backgroundColors[defrosterViewModel.heatingState]!!, 0.5f)
    } }
    var targetTempSliderValue by remember { mutableFloatStateOf(
        defrosterViewModel.targetTemp.toFloat()
    ) }
    val hintTextText by remember { derivedStateOf {
        if (
            defrosterViewModel.targetTemp <= defrosterViewModel.currentTemp
            && defrosterViewModel.heatingState == HeatingState.NOT_HEATING
            ) {
            "Target temp. must exceed current"
        } else {
            "Keeps temp. between ${
                defrosterViewModel.targetTempLowerLimit
            } and ${
                defrosterViewModel.targetTempUpperLimit
            } °C"
        }
    } }
    val isToggleHeatingButtonEnabled by remember { derivedStateOf {
        when (defrosterViewModel.heatingState) {
            HeatingState.HEATING -> true
            HeatingState.STOPPING_HEATING -> false
            HeatingState.NOT_HEATING -> defrosterViewModel.targetTemp > defrosterViewModel.currentTemp
        }
    } }
    val toggleHeatingButtonText by remember { derivedStateOf {
        when (defrosterViewModel.heatingState) {
            HeatingState.HEATING -> "Stop"
            HeatingState.STOPPING_HEATING -> "Stopping"
            HeatingState.NOT_HEATING -> "Start"
        }
    } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(
                colors = listOf(Color.White, backgroundColors[defrosterViewModel.heatingState]!!),
                radius = maxOf(LocalConfiguration.current.screenWidthDp, LocalConfiguration.current.screenHeightDp).toFloat() * 3f
            ))
            .padding(16.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Defrost",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = "Current Temp.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 35.sp,
                        color = currentTempColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${"%.2f".format(defrosterViewModel.currentTemp)} °C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 55.sp,
                        color = currentTempColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        thickness = 3.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Target Temp.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 35.sp,
                        color = targetTempColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${defrosterViewModel.targetTemp} °C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 55.sp,
                        color = targetTempColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = targetTempSliderValue,
                        onValueChange = {
                            val itInt = it.roundToInt()
                            targetTempSliderValue = itInt.toFloat()
                            defrosterViewModel.targetTemp = itInt
                                        },
                        valueRange = 10f..90f,
                        modifier = Modifier.fillMaxWidth(0.9f),
                        enabled = defrosterViewModel.heatingState != HeatingState.HEATING,
                        colors = SliderDefaults.colors(
                            thumbColor = targetTempColor,
                            activeTrackColor = targetTempColor,
                            disabledThumbColor = targetTempDisabledColor,
                            disabledActiveTrackColor = targetTempDisabledColor
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 10..90 step 10) {
                            Text(
                                text = i.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 20.sp,
                                color = lerp(coldColor, hotColor, i.mapToRange(30f, 70f))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = hintTextText,
                        fontSize = 20.sp,
                        color = targetTempColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            defrosterViewModel.toggleHeating()
                        },
                        enabled = isToggleHeatingButtonEnabled,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = toggleHeatingButtonText, fontSize = 25.sp)
                    }

                }
            }
        }
    }
}
