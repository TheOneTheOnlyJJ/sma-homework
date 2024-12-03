package com.example.smaproject

import DefrostViewModel
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
    val defrostViewModel: DefrostViewModel = viewModel()
    val navController = rememberNavController()
    val sensorManager: SensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val ambientTempSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    val ambientTempSensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                var newAmbientTemp = event.values[0]
                Log.i(
                    "Defroster App",
                    "Ambient temperature changed to: ${newAmbientTemp}."
                )
                if (newAmbientTemp < -273.15f) {
                    Log.i(
                        "Defroster App",
                        "Abnormally low temperature received. Capping at absolute zero."
                    )
                    newAmbientTemp = -273.15f
                }
                defrostViewModel.currentTemp.floatValue = newAmbientTemp
            }
        }
    }
    if (ambientTempSensor != null) {
        Log.i("Defroster App", "Registering ambient temperature sensor listener.")
        sensorManager.registerListener(
            ambientTempSensorEventListener,
            ambientTempSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        Log.i("Defroster App", "Registered ambient temperature sensor listener.")
    } else {
        Log.i("Defroster App", "Null ambient temperature sensor.")
    }
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, defrostViewModel) }
        composable("slider") { SliderScreen(navController, defrostViewModel) }
        composable("list") { ListScreen(navController, defrostViewModel) }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DefrosterApp()
}
