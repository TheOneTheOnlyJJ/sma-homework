package com.jurjandreigeorge.defroster.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jurjandreigeorge.defroster.presentation.theme.getTempColor

@Composable
fun MinTargetMaxTempRow(
    minTemp: Float,
    targetTemp: Int,
    maxTemp: Float,
    textColor: Color
) {
    val minTempColor = remember { getTempColor(minTemp) }
    val targetTempColor = remember { getTempColor(targetTemp) }
    val maxTempColor = remember { getTempColor(maxTemp) }

    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().weight(2f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Min.",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                color = textColor
            )
            Text(
                text = "${"%.2f".format(minTemp)} °C",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = minTempColor
            )
        }
        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            thickness = 2.dp
        )
        Column(
            modifier = Modifier.fillMaxWidth().weight(1.4f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Target",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                color = textColor
            )
            Text(
                text = "$targetTemp °C",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = targetTempColor
            )
        }
        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            thickness = 2.dp
        )
        Column(
            modifier = Modifier.fillMaxWidth().weight(2f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Max.",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                color = textColor
            )
            Text(
                text = "${"%.2f".format(maxTemp)} °C",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = maxTempColor
            )
        }
    }
}