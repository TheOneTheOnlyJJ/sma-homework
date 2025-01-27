package com.jurjandreigeorge.defroster.presentation.ui.component

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jurjandreigeorge.defroster.presentation.viewmodel.DefrosterViewModel

@Composable
fun HeatingStatsCountThresholdReachedDialog(
    logTag: String = "Heating Stats Count Threshold Reached Dialog",
    defrosterViewModel: DefrosterViewModel,
    navController: NavController
) {
    var isHeatingStatsThresholdDialogOpen by remember { mutableStateOf(false) }
    val currentHeatingStatsCount by defrosterViewModel.heatingStatsCountFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    LaunchedEffect(currentHeatingStatsCount) {
        if (
            !defrosterViewModel.hasWarnedUserOfHeatingStatsCountThreshold
            &&
            currentHeatingStatsCount >= defrosterViewModel.heatingStatsCountThreshold
            ) {
            isHeatingStatsThresholdDialogOpen = true
            defrosterViewModel.hasWarnedUserOfHeatingStatsCountThreshold = true
        }
    }

    when {
        isHeatingStatsThresholdDialogOpen -> {
            AlertDialog(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.BarChart,
                        contentDescription = "Analytical Chart Illustration"
                    )
                },
                title = {
                    Text("Too Many Stats?")
                },
                text = {
                    Text(
                        "The maximum recommended defrost stats count is " +
                                "${defrosterViewModel.heatingStatsCountThreshold}. " +
                                "You currently have $currentHeatingStatsCount. " +
                                "Consider deleting older stats to save on device memory."
                    )
                },
                onDismissRequest = {
                    Log.i(logTag, "Request dismissed.")
                    isHeatingStatsThresholdDialogOpen = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        Log.i(logTag, "\"To Activity\" button clicked.")
                        navController.navigate("activity")
                    }) {
                        Text("To Activity")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        Log.i(logTag, "\"I'll keep 'em\" button clicked.")
                        isHeatingStatsThresholdDialogOpen = false
                    }) {
                        Text("I'll keep 'em")
                    }
                }
            )
        }
    }

}