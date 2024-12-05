package com.example.smaproject

import DefrosterViewModel
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefrosterApp()
        }
    }
}

@Composable
fun DefrosterApp() {
    val defrosterViewModel: DefrosterViewModel = viewModel()
    val navController = rememberNavController()
    val sensorManager: SensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val ambientTempSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    val ambientTempSensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                var newAmbientTemp = event.values[0]
                Log.i(
                    "Defroster",
                    "Ambient temperature changed to: ${newAmbientTemp}."
                )
                if (newAmbientTemp < -273.15f) {
                    Log.i(
                        "Defroster",
                        "Abnormally low ambient temperature received. Capping at absolute zero."
                    )
                    newAmbientTemp = -273.15f
                }
                defrosterViewModel.currentTemp.floatValue = newAmbientTemp
            }
        }
    }
    if (ambientTempSensor != null) {
        Log.i("Defroster", "Registering ambient temperature sensor listener.")
        sensorManager.registerListener(
            ambientTempSensorEventListener,
            ambientTempSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        Log.i("Defroster", "Registered ambient temperature sensor listener.")
    } else {
        Log.i("Defroster", "Null ambient temperature sensor.")
    }
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, defrosterViewModel) }
        composable("slider") { SliderScreen(navController, defrosterViewModel) }
        composable("list") { ListScreen(navController, defrosterViewModel) }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DefrosterApp()
}
