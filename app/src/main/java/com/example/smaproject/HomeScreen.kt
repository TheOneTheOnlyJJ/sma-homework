package com.example.smaproject

import DefrostViewModel
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController, defrostViewModel: DefrostViewModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(
                colors = listOf(Color.White, defrostViewModel.backgroundColor.value),
                radius = maxOf(LocalConfiguration.current.screenWidthDp, LocalConfiguration.current.screenHeightDp).toFloat() * 2f
            ))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Defroster",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 70.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Image(
            painter = painterResource(id = R.drawable.defroster_icon),
            contentDescription = "Defroster Image",
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
        )
        Text(
            text = "Your portable defroster\nWhen snow can't melt fast enough",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("slider") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Defrost", fontSize = 25.sp)
            }
            Button(
                onClick = { navController.navigate("list") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Activity", fontSize = 25.sp)
            }
        }
    }
}