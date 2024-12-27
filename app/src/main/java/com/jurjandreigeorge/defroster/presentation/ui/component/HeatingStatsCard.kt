package com.jurjandreigeorge.defroster.presentation.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jurjandreigeorge.defroster.data.HeatingStatsEntity

@Composable
fun HeatingStatsCard(
    heatingStats: HeatingStatsEntity,
    title: String,
    onCheckboxValueChange: (Boolean) -> Unit,
    onExpandArrowClick: () -> Unit,
    isSelected: Boolean,
    isExpanded: Boolean
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "Expand card arrow rotation animation"
    )
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
            .padding(top = 8.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
        border = CardDefaults.outlinedCardBorder(enabled = isSelected)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .basicMarquee(iterations = Int.MAX_VALUE),
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left,
                    color = textColor
                )
                IconButton(
                    modifier = Modifier
                        .scale(1.25f)
                        .rotate(arrowRotation),
                    onClick = onExpandArrowClick
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            Checkbox(
                modifier = Modifier.width(IntrinsicSize.Min),
                checked = isSelected,
                onCheckedChange = onCheckboxValueChange
            )
        }
        if (isExpanded) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                HeatingTempStats(
                    startTemp = heatingStats.startTemp,
                    endTemp = heatingStats.endTemp,
                    minTemp = heatingStats.minTemp,
                    targetTemp = heatingStats.targetTemp,
                    maxTemp = heatingStats.maxTemp,
                    textColor = textColor
                )
                HeatingTimeSeriesChart(
                    heatingStats = heatingStats
                )
                HeatingTimeStats(
                    startTime = heatingStats.startTime,
                    endTime = heatingStats.endTime,
                    textColor = textColor
                )
            }
        }
    }
}
