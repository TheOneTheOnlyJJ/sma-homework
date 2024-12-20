package com.jurjandreigeorge.defroster.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jurjandreigeorge.defroster.presentation.theme.getBackgroundColorGradient
import com.jurjandreigeorge.defroster.presentation.ui.component.HeatingStatsCard
import com.jurjandreigeorge.defroster.presentation.viewmodel.DefrosterViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    navController: NavController,
    defrosterViewModel: DefrosterViewModel
) {
    val nonDeletedHeatingStats by defrosterViewModel.nonDeletedHeatingStatsFlow.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val selectedHeatingStatsIds = remember { mutableStateListOf<Long>() }

    var isDeleteHeatingStatsDialogOpen by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        canScroll = { scrollState.canScrollForward || scrollState.canScrollBackward }
    )

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
                    selectedHeatingStatsIds.clear()
                    isDeleteHeatingStatsDialogOpen = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        Log.i("Defroster Activity Screen", "Delete items dialog Confirm button clicked.")
                        defrosterViewModel.deleteHeatingStats(selectedHeatingStatsIds.toList())
                        selectedHeatingStatsIds.clear()
                        isDeleteHeatingStatsDialogOpen = false
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        Log.i("Defroster Activity Screen", "Delete items dialog Dismiss button clicked.")
                        selectedHeatingStatsIds.clear()
                        isDeleteHeatingStatsDialogOpen = false
                    }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }

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
                title = { Text("Activity") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                scrollBehavior = scrollBehavior,
                actions = {
                    if (selectedHeatingStatsIds.isNotEmpty()) {
                        IconButton(onClick = {
                            isDeleteHeatingStatsDialogOpen = true
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteForever,
                                contentDescription = "Delete all selected heating stats"
                            )
                        }
                    }
                    if (nonDeletedHeatingStats.size >= 2) {
                        IconButton(onClick = {
                            if (defrosterViewModel.isHeatingCardListReversed) {
                                Log.i("Defroster", "Un-reversing heating stats list.")
                                defrosterViewModel.isHeatingCardListReversed = false
                            } else {
                                Log.i("Defroster", "Reversing heating stats list.")
                                defrosterViewModel.isHeatingCardListReversed = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.SwapVert,
                                contentDescription = "Reverse heating stats list"
                            )
                        }
                    }
                },
            )
        }
    ) {
        innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColorGradient(defrosterViewModel.heatingState))
                .padding(innerPadding)
        ) {
            if (nonDeletedHeatingStats.isEmpty()) {
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
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    state = scrollState
                ) {
                    items(nonDeletedHeatingStats.size) { index ->
                        val currentHeatingStats = if (defrosterViewModel.isHeatingCardListReversed)
                            nonDeletedHeatingStats.reversed()[index]
                        else
                            nonDeletedHeatingStats[index]
                        HeatingStatsCard(
                            heatingStats = currentHeatingStats,
                            title = "Defrost #${currentHeatingStats.id}",
                            onLongClick = {
                                scope.launch {
                                    Log.i("Defroster Activity Screen", "Long click detected on heating stats ${currentHeatingStats.id} card.")
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (selectedHeatingStatsIds.contains(currentHeatingStats.id)) {
                                        Log.i("Defroster Activity Screen", "Deselecting heating stats ${currentHeatingStats.id} card.")
                                        selectedHeatingStatsIds.remove(currentHeatingStats.id)
                                    } else {
                                        Log.i("Defroster Activity Screen", "Selecting heating stats ${currentHeatingStats.id} card.")
                                        selectedHeatingStatsIds.add(currentHeatingStats.id)
                                    }
                                }
                            },
                            isSelected = selectedHeatingStatsIds.contains(currentHeatingStats.id),
                            showSyncStatus = true
                        )
                    }
                }
            }
        }
    }
}
