package com.jurjandreigeorge.defroster.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jurjandreigeorge.defroster.presentation.theme.coldColor
import com.jurjandreigeorge.defroster.presentation.theme.getBackgroundColorGradient
import com.jurjandreigeorge.defroster.presentation.theme.hotColor
import com.jurjandreigeorge.defroster.presentation.ui.component.HeatingStatsCard
import com.jurjandreigeorge.defroster.presentation.viewmodel.DefrosterViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    defrosterViewModel: DefrosterViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundColorGradient(defrosterViewModel.heatingState))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Defroster",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 70.sp,
                    fontWeight = FontWeight.Bold,
                    brush = Brush.horizontalGradient(
                        colorStops = arrayOf(0.25f to coldColor, 0.75f to hotColor)
                    )
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(
                    id = com.jurjandreigeorge.defroster.R.drawable.defroster_icon
                ),
                contentDescription = "Defroster Image",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f)
            )
            Text(
                text = "When snow can’t melt fast enough",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    Log.i("Defroster Navigation", "Clicked on Defrost button.")
                    navController.navigate("defrost")
                    Log.i("Defroster Navigation", "Navigated to Defrost screen.")
                },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text(text = "Defrost", fontSize = 25.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    Log.i("Defroster Navigation", "Clicked on Defrost button.")
                    navController.navigate("activity")
                    Log.i("Defroster Navigation", "Navigated to Activity screen.")
                },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text(text = "Activity", fontSize = 25.sp)
            }
            if (defrosterViewModel.latestDefrost != null) {
                Spacer(modifier = Modifier.height(32.dp))
                HeatingStatsCard(
                    heatingStats = defrosterViewModel.latestDefrost!!,
                    title = "Latest Defrost"
                )
            }
        }
    }
}
