package com.jurjandreigeorge.defroster.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jurjandreigeorge.defroster.domain.HeatingState
import com.jurjandreigeorge.defroster.presentation.theme.getBackgroundColorGradient
import com.jurjandreigeorge.defroster.presentation.theme.getDisabledTempColor
import com.jurjandreigeorge.defroster.presentation.theme.getTempColor
import com.jurjandreigeorge.defroster.presentation.viewmodel.DefrosterViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefrostScreen(
    navController: NavController,
    defrosterViewModel: DefrosterViewModel
) {
    val currentTempColor by remember {
        derivedStateOf { getTempColor(defrosterViewModel.currentTemp) }
    }
    val targetTempColor by remember {
        derivedStateOf { getTempColor(defrosterViewModel.targetTemp) }
    }
    val targetTempDisabledColor by remember {
        derivedStateOf { getDisabledTempColor(targetTempColor, defrosterViewModel.heatingState) }
    }
    var targetTempSliderValue by remember {
        mutableFloatStateOf(defrosterViewModel.targetTemp.toFloat())
    }
    val hintTextText by remember {
        derivedStateOf {
            if (
                defrosterViewModel.targetTemp <= defrosterViewModel.currentTemp
                && defrosterViewModel.heatingState == HeatingState.NOT_HEATING
            ) {
                "Target temp. must exceed current"
            } else {
                "Keeps temp. at ${
                    defrosterViewModel.targetTempLowerLimit
                } – ${
                    defrosterViewModel.targetTempUpperLimit
                } °C"
            }
        }
    }
    val isToggleHeatingButtonEnabled by remember {
        derivedStateOf {
            when (defrosterViewModel.heatingState) {
                HeatingState.HEATING -> true
                HeatingState.STOPPING_HEATING -> false
                HeatingState.NOT_HEATING -> defrosterViewModel.targetTemp > defrosterViewModel.currentTemp
            }
        }
    }
    val toggleHeatingButtonText by remember {
        derivedStateOf {
            when (defrosterViewModel.heatingState) {
                HeatingState.HEATING -> "Stop"
                HeatingState.STOPPING_HEATING -> "Stopping"
                HeatingState.NOT_HEATING -> "Start"
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        Log.i("Defroster Navigation", "Clicked back arrow.")
                        navController.popBackStack()
                        Log.i("Defroster Navigation", "Popped back stack.")
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                title = { Text("Defrost") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColorGradient(defrosterViewModel.heatingState))
                .padding(innerPadding)
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
                    fontWeight = FontWeight.Medium,
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
                    fontWeight = FontWeight.Medium,
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
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = hintTextText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
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
