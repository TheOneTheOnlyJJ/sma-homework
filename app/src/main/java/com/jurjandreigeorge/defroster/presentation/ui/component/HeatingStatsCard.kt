package com.jurjandreigeorge.defroster.presentation.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jurjandreigeorge.defroster.data.HeatingStatsEntity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeatingStatsCard(
    heatingStats: HeatingStatsEntity,
    title: String,
    onLongClick: () -> Unit = {},
    isSelected: Boolean = false
) {
    val cardContainerColor: Color
    val textColor: Color
    if (isSelected) {
        cardContainerColor = MaterialTheme.colorScheme.surfaceVariant
        textColor = MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        cardContainerColor = MaterialTheme.colorScheme.surface
        textColor = MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick,
                onClickLabel = "Delete Heating Stats"
            ),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
        border = CardDefaults.outlinedCardBorder(enabled = isSelected)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            HeatingTempStats(
                startTemp = heatingStats.startTemp,
                endTemp = heatingStats.endTemp,
                minTemp = heatingStats.minTemp,
                targetTemp = heatingStats.targetTemp,
                maxTemp = heatingStats.maxTemp,
                textColor = textColor
            )
            HeatingTimeSeriesChart(
                heatingStats = heatingStats,
                textColor = textColor,
                cardContainerColor = cardContainerColor
            )
            HeatingTimeStats(
                startTime = heatingStats.startTime,
                endTime = heatingStats.endTime,
                textColor = textColor
            )
        }
    }
}
