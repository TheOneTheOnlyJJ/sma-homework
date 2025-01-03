package com.jurjandreigeorge.defroster.presentation.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jurjandreigeorge.defroster.domain.dateTimePattern
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HeatingTimeStats(
    startTime: String,
    endTime: String,
    textColor: Color
) {
    val formatter = remember { DateTimeFormatter.ofPattern(dateTimePattern) }
    val duration = remember { Duration.between(
        LocalDateTime.parse(startTime, formatter),
        LocalDateTime.parse(endTime, formatter)
    ) }
    val hours = remember { duration.toHours() }
    val minutes = remember { duration.toMinutes() % 60 }
    val seconds = remember { duration.seconds % 60 }

    val formattedDuration = remember { buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m ")
        append("${seconds}s")
    }.trim() }

    Column(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
    ) {
        DataRow(
            data = "Duration",
            dataTextColor = textColor,
            value = formattedDuration,
            valueTextColor = textColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        DataRow(
            data = "From",
            dataTextColor = textColor,
            value = startTime,
            valueTextColor = textColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        DataRow(
            data = "To",
            dataTextColor = textColor,
            value = endTime,
            valueTextColor = textColor
        )
    }
}
