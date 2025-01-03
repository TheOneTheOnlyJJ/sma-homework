package com.jurjandreigeorge.defroster.presentation.ui.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun StartToEndTempRow(
    startTemp: Float,
    endTemp: Float,
    textColor: Color
) {
    val startTempColor = remember { getTempColor(startTemp) }
    val endTempColor = remember { getTempColor(endTemp) }

    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                text = "Start",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                text = "${"%.2f".format(startTemp)} °C",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = startTempColor
            )
        }
        Icon(
            imageVector = Icons.Rounded.DoubleArrow,
            contentDescription = "Start to end temperature arrow",
            modifier = Modifier.padding(horizontal = 4.dp).size(48.dp),
            tint = textColor
        )
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                text = "End",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                text = "${"%.2f".format(endTemp)} °C",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = endTempColor
            )
        }
    }
}
