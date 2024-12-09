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
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
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
    val backgroundColors = remember { mapOf(
        HeatingState.HEATING to Color(0xFFFFB3B3),
        HeatingState.STOPPING_HEATING to Color(0xFFFFB3B3),
        HeatingState.NOT_HEATING to Color(0xFFA2F2F0)
    ) }
    val sensorManager: SensorManager = LocalContext.current.getSystemService(
        Context.SENSOR_SERVICE
    ) as SensorManager
    val ambientTempSensor: Sensor? = sensorManager.getDefaultSensor(
        Sensor.TYPE_AMBIENT_TEMPERATURE
    )
    val ambientTempSensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            if (sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                var sensorStatus = "UNKNOWN ACCURACY CODE"
                when (accuracy) {
                    SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                        sensorStatus = "UNRELIABLE"
                    }
                    SensorManager.SENSOR_STATUS_NO_CONTACT -> {
                        sensorStatus = "NO CONTACT"
                    }
                    SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
                        sensorStatus = "ACCURACY LOW"
                    }
                    SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> {
                        sensorStatus = "ACCURACY MEDIUM"
                    }
                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
                        sensorStatus = "ACCURACY HIGH"
                    }
                }
                Log.i(
                    "Defroster",
                    "Ambient temperature sensor accuracy changed: ${sensorStatus}."
                )
            }
        }
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
                defrosterViewModel.currentTemp = newAmbientTemp
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
        composable("home") { HomeScreen(
            navController,
            defrosterViewModel,
            backgroundColors
        ) }
        composable("slider") { SliderScreen(
            navController,
            defrosterViewModel,
            backgroundColors
        ) }
        composable("list") { ListScreen(
            navController,
            defrosterViewModel,
            backgroundColors
        ) }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DefrosterApp()
}
