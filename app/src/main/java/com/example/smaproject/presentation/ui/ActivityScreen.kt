package com.example.smaproject.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.smaproject.domain.HeatingState
import com.example.smaproject.data.HeatingStats
import com.example.smaproject.presentation.DefrosterViewModel

@Composable
fun ActivityScreen(
    navController: NavController,
    defrosterViewModel: DefrosterViewModel,
    backgroundColors: Map<HeatingState, Color>
) {
    val heatingStats by defrosterViewModel.heatingStatsFlow.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val selectedHeatingStats = remember { mutableStateListOf<HeatingStats>() }
    val haptics = LocalHapticFeedback.current

    var isDeleteHeatingStatsDialogOpen by remember { mutableStateOf(false) }

    when {
        isDeleteHeatingStatsDialogOpen -> {
            AlertDialog(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.DeleteForever,
                        contentDescription = "Delete selected heating stats"
                    )
                },
                title = {
                    Text("Delete Heating Stats")
                },
                text = {
                    Text("Are you sure you want to delete the selected heating stats?")
                },
                onDismissRequest = {
                    selectedHeatingStats.clear()
                    isDeleteHeatingStatsDialogOpen = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        defrosterViewModel.deleteHeatingStats(*selectedHeatingStats.toTypedArray())
                        selectedHeatingStats.clear()
                        isDeleteHeatingStatsDialogOpen = false
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        selectedHeatingStats.clear()
                        isDeleteHeatingStatsDialogOpen = false
                    }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(
                colors = listOf(Color.White, backgroundColors[defrosterViewModel.heatingState]!!),
                radius = maxOf(
                    LocalConfiguration.current.screenWidthDp,
                    LocalConfiguration.current.screenHeightDp
                ).toFloat() * 3f
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
                text = "Activity",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (heatingStats.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No activity to show",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(heatingStats.size) { index ->
                        HeatingStatsCard(
                            heatingStats = heatingStats[index],
                            defrosterViewModel = defrosterViewModel,
                            onClick = {
                                if (selectedHeatingStats.contains(heatingStats[index])) {
                                    selectedHeatingStats.remove(heatingStats[index])
                                } else {
                                    selectedHeatingStats.add(heatingStats[index])
                                }
                            },
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (!selectedHeatingStats.contains(heatingStats[index])) {
                                    selectedHeatingStats.add(heatingStats[index])
                                }
                                isDeleteHeatingStatsDialogOpen = true
                            },
                            isSelected = selectedHeatingStats.contains(heatingStats[index])
                        )
                    }
                }
            }
        }
    }
}
