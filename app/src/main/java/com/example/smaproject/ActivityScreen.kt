package com.example.smaproject

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ActivityScreen(
    navController: NavController,
    defrosterViewModel: DefrosterViewModel,
    backgroundColors: Map<HeatingState, Color>
) {
    val heatingStats by defrosterViewModel.heatingStatsDao!!
        .loadAll()
        .collectAsStateWithLifecycle(
            initialValue = emptyList()
        )

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
                            defrosterViewModel = defrosterViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeatingStatsCard(heatingStats: HeatingStats, defrosterViewModel: DefrosterViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Defrost #${heatingStats.id}",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.i(
                                "Defroster Database Deletion Coroutine",
                                "Deleting heating stats ${heatingStats.id}."
                            )
                            val deletedId = defrosterViewModel.heatingStatsDao!!.delete(heatingStats)
                            Log.i(
                                "Defroster Database Deletion Coroutine",
                                "Deleted heating stats. Inserted ID: $deletedId."
                            )
                        }
                    }
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete entry")
                }
            }
            Text(
                text = "Start time: ${heatingStats.startTime}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "End time: ${heatingStats.endTime}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start temp.: ${heatingStats.startTemp}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "End temp.: ${heatingStats.endTemp}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Target temp.: ${heatingStats.targetTemp}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
