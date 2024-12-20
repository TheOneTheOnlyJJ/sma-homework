package com.jurjandreigeorge.defroster.presentation.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jurjandreigeorge.defroster.presentation.ui.screen.ActivityScreen
import com.jurjandreigeorge.defroster.presentation.ui.screen.DefrostScreen
import com.jurjandreigeorge.defroster.presentation.ui.screen.HomeScreen
import com.jurjandreigeorge.defroster.presentation.viewmodel.DefrosterViewModel

@Composable
fun DefrosterApp() {
    val defrosterViewModel: DefrosterViewModel = hiltViewModel()
    val navController = rememberNavController()
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
        composable("home") {
            HomeScreen(navController, defrosterViewModel)
        }
        composable("defrost") {
            DefrostScreen(navController,defrosterViewModel)
        }
        composable("activity") {
            ActivityScreen(navController, defrosterViewModel)
        }
    }
}