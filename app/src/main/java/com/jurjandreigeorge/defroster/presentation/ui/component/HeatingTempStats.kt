package com.jurjandreigeorge.defroster.presentation.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HeatingTempStats(
    startTemp: Float,
    endTemp: Float,
    minTemp: Float,
    targetTemp: Int,
    maxTemp: Float,
    textColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StartToEndTempRow(
            startTemp = startTemp,
            endTemp = endTemp,
            textColor = textColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        MinTargetMaxTempRow(
            minTemp = minTemp,
            targetTemp = targetTemp,
            maxTemp = maxTemp,
            textColor = textColor
        )
    }
}
