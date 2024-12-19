package com.example.defroster.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.defroster.data.HeatingStats
import com.example.defroster.domain.dateTimePattern
import com.example.defroster.presentation.theme.getTempColor
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeatingStatsCard(
    heatingStats: HeatingStats,
    onLongClick: () -> Unit,
    isSelected: Boolean
) {
    val startTempColor = getTempColor(heatingStats.startTemp)
    val endTempColor = getTempColor(heatingStats.endTemp)
    val targetTempColor = getTempColor(heatingStats.targetTemp)

    val formatter = DateTimeFormatter.ofPattern(dateTimePattern)
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
                onClick = {},
                onLongClick = onLongClick,
                onClickLabel = "Delete Heating Stats"
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = CardDefaults.outlinedCardBorder(enabled = isSelected)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Defrost #${heatingStats.id}",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${"%.2f".format(heatingStats.startTemp)} °C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = startTempColor
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.DoubleArrow,
                    contentDescription = "Start to end temperature icon",
                    modifier = Modifier.padding(horizontal = 8.dp).size(32.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${"%.2f".format(heatingStats.endTemp)} °C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = endTempColor
                    )
                }
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
                    fontWeight = FontWeight.Bold,
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
