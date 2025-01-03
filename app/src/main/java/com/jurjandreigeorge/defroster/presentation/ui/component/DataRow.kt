package com.jurjandreigeorge.defroster.presentation.ui.component

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun DataRow(
    data: String,
    dataFontWeight: FontWeight = FontWeight.Normal,
    dataTextColor: Color = Color.Unspecified,
    value: String,
    valueFontWeight: FontWeight = FontWeight.Normal,
    valueTextColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
            text = "$data:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = dataFontWeight,
            color = dataTextColor,
            fontSize = 20.sp
        )
        Text(
            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = valueFontWeight,
            color = valueTextColor,
            fontSize = 20.sp
        )
    }
}
