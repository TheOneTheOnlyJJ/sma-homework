package com.example.smaproject

import DefrosterViewModel
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.roundToInt

@Composable
fun SliderScreen(navController: NavController, defrosterViewModel: DefrosterViewModel) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(
                colors = listOf(Color.White, defrosterViewModel.backgroundColor.value),
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
                        fontSize = 30.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${"%.2f".format(defrosterViewModel.currentTemp.floatValue)} °C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 50.sp
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
                        fontSize = 30.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${defrosterViewModel.targetTemp.floatValue.toInt()} °C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 50.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = defrosterViewModel.targetTemp.floatValue,
                        onValueChange = { defrosterViewModel.targetTemp.floatValue = it.roundToInt().toFloat() },
                        valueRange = 10f..90f,
                        modifier = Modifier.fillMaxWidth(0.9f),
                        enabled = defrosterViewModel.isTargetTempSliderEnabled.value
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
                                fontSize = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            defrosterViewModel.toggleHeating()
                        },
                        enabled = defrosterViewModel.isToggleHeatingButtonEnabled.value,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(defrosterViewModel.toggleHeatingButtonText.value, fontSize = 25.sp)
                    }

                }
            }
        }
    }
}
