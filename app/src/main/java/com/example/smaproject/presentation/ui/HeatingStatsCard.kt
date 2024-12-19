package com.example.smaproject.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smaproject.data.HeatingStats
import com.example.smaproject.presentation.DefrosterViewModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeatingStatsCard(
    heatingStats: HeatingStats,
    defrosterViewModel: DefrosterViewModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean
) {
    fun Float.mapToRange(min: Float, max: Float): Float {
        return (this - min) / (max - min)
    }
    fun Int.mapToRange(min: Float, max: Float): Float {
        return (this - min) / (max - min)
    }
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

    val startTempColor = lerp(
        defrosterViewModel.coldColor,
        defrosterViewModel.hotColor,
        heatingStats.startTemp.mapToRange(30f, 70f)
    )
    val endTempColor = lerp(
        defrosterViewModel.coldColor,
        defrosterViewModel.hotColor,
        heatingStats.endTemp.mapToRange(30f, 70f)
    )
    val targetTempColor = lerp(
        defrosterViewModel.coldColor,
        defrosterViewModel.hotColor,
        heatingStats.targetTemp.mapToRange(30f, 70f)
    )
    val duration = Duration.between(
        LocalDateTime.parse(heatingStats.startTime, formatter),
        LocalDateTime.parse(heatingStats.endTime, formatter)
    )
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60

    val formattedDuration = buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m ")
        append("${seconds}s")
    }.trim()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                onClickLabel = "Delete Heating Stats"
            ),
        border = if (isSelected) BorderStroke(1.0.dp, Color.Red) else CardDefaults.outlinedCardBorder(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Defrost #${heatingStats.id}",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${"%.2f".format(heatingStats.startTemp)} °C",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 30.sp,
                    color = startTempColor
                )
                Icon(
                    imageVector = Icons.Rounded.DoubleArrow,
                    contentDescription = "Start to end temperature icon"
                )
                Text(
                    text = "${"%.2f".format(heatingStats.endTemp)} °C",
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 30.sp,
                    color = endTempColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Target temperature:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )
                Text(
                    text= "${heatingStats.targetTemp} °C",
                    style = MaterialTheme.typography.bodyLarge,
                    color = targetTempColor,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Start time:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )
                Text(
                    text = heatingStats.startTime,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "End time:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )
                Text(
                    text = heatingStats.endTime,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Duration:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )
                Text(
                    text = formattedDuration,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )
            }
        }
    }
}
