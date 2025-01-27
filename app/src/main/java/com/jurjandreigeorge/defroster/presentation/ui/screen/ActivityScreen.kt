package com.jurjandreigeorge.defroster.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.UnfoldLess
import androidx.compose.material.icons.rounded.UnfoldMore
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
    val currentHeatingStats by defrosterViewModel.allHeatingStatsFlow.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val expandedHeatingStatsIds = remember { mutableStateListOf<Long>() }
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
                    Log.i("Defroster Activity Screen", "Dialog request dismissed.")
                    selectedHeatingStatsIds.clear()
                    isDeleteHeatingStatsDialogOpen = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        Log.i("Defroster Activity Screen", "Delete items dialog Confirm button clicked.")
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        defrosterViewModel.deleteHeatingStats(selectedHeatingStatsIds.toList())
                        expandedHeatingStatsIds.removeAll(selectedHeatingStatsIds)
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
                    if (expandedHeatingStatsIds.isNotEmpty()) {
                        IconButton(onClick = {
                            Log.i("Defroster Activity Screen", "Collapse all heating stats cards icon button clicked.")
                            expandedHeatingStatsIds.clear()
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.UnfoldLess,
                                contentDescription = "Collapse all heating stats cards"
                            )
                        }
                    }
                    if (expandedHeatingStatsIds.size < currentHeatingStats.size) {
                        IconButton(onClick = {
                            Log.i("Defroster Activity Screen", "Expand all heating stats cards icon button clicked.")
                            for (heatingStats in currentHeatingStats) {
                                if (!expandedHeatingStatsIds.contains(heatingStats.id)) {
                                    expandedHeatingStatsIds.add(heatingStats.id)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.UnfoldMore,
                                contentDescription = "Expand all heating stats cards"
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
            if (currentHeatingStats.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                        text = "No activity to show",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    state = scrollState
                ) {
                    items(currentHeatingStats.size) { index ->
                        val heatingStats = currentHeatingStats[index]
                        HeatingStatsCard(
                            heatingStats = heatingStats,
                            title = "Defrost #${heatingStats.id}",
                            onCheckboxValueChange = { checked ->
                                scope.launch {
                                    Log.i("Defroster Activity Screen", "Checkbox value changed to $checked for heating stats ${heatingStats.id} card.")
                                    if (checked) {
                                        Log.i("Defroster Activity Screen", "Selecting heating stats ${heatingStats.id} card.")
                                        selectedHeatingStatsIds.add(heatingStats.id)
                                    } else {
                                        Log.i("Defroster Activity Screen", "Deselecting heating stats ${heatingStats.id} card.")
                                        selectedHeatingStatsIds.remove(heatingStats.id)
                                    }
                                }
                            },
                            onExpandArrowClick = {
                                scope.launch {
                                    Log.i("Defroster Activity Screen", "Expand arrow clicked for heating stats ${heatingStats.id} card.")
                                    if (expandedHeatingStatsIds.contains(heatingStats.id)) {
                                        Log.i("Defroster Activity Screen", "Removing heating stats ${heatingStats.id} from expanded list.")
                                        expandedHeatingStatsIds.remove(heatingStats.id)
                                    } else {
                                        Log.i("Defroster Activity Screen", "Adding heating stats ${heatingStats.id} to expanded list.")
                                        expandedHeatingStatsIds.add(heatingStats.id)
                                    }
                                }
                            },
                            isSelected = selectedHeatingStatsIds.contains(heatingStats.id),
                            isExpanded = expandedHeatingStatsIds.contains(heatingStats.id)
                        )
                    }
                }
            }
        }
    }
}
