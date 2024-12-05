import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DefrosterViewModel : ViewModel() {
    private val coldColor = Color(0xffa2f2f0)
    private val hotColor = Color(0xffff9a90)

    val currentTemp = mutableFloatStateOf(0f)
    val targetTemp = mutableFloatStateOf(10f)
    val toggleHeatingButtonText = mutableStateOf("Start")
    val isToggleHeatingButtonEnabled = mutableStateOf(true)
    private val isHeatingStarted = mutableStateOf(false)
    val isTargetTempSliderEnabled = mutableStateOf(true)
    val backgroundColor = mutableStateOf(coldColor)
    private val heatingThreads = mutableStateListOf<Thread>()
    private val stopHeatingTrigger = MutableStateFlow(false)

    init {
        observeStopHeatingTrigger()
        observeTempChanges()
    }

    private fun observeStopHeatingTrigger() {
        CoroutineScope(Dispatchers.Default).launch {
            stopHeatingTrigger.collectLatest { shouldStop ->
                if (shouldStop) {
                    stopHeating()
                }
            }
        }
    }

    private fun observeTempChanges() {
        val currentTempFlow = snapshotFlow { currentTemp.floatValue }
        val targetTempFlow = snapshotFlow { targetTemp.floatValue }

        CoroutineScope(Dispatchers.Default).launch {
            combine(
                currentTempFlow,
                targetTempFlow
            ) { current, target ->
                target.roundToInt() > current
            }.collectLatest { isTargetTempGreaterThanCurrentTemp ->
                isToggleHeatingButtonEnabled.value = isTargetTempGreaterThanCurrentTemp
            }
        }
    }

    fun toggleHeating() {
        this.isHeatingStarted.value = !isHeatingStarted.value
        if (this.isHeatingStarted.value) {
            this.startHeating()
        } else {
            this.stopHeatingTrigger.value = true
        }
    }

    private fun startHeating() {
        Log.i(
            "Defroster",
            "Starting heating with target temperature of ${this.targetTemp.floatValue.roundToInt()} °C."
        )
        this.toggleHeatingButtonText.value = "Stop"
        this.isTargetTempSliderEnabled.value = false
        this.backgroundColor.value = this.hotColor
        val availableProcessors = Runtime.getRuntime().availableProcessors()
        Log.i("Defroster", "Device has $availableProcessors available processors.")
        for (i in 1..availableProcessors) {
            val newHeatingThread = Thread {
                val logTag = "Defroster Heating Thread No. $i"
                val iterationCycleLoops = 10_000_000
                try {
                    Log.i(logTag, "Starting.")
                    var currentIterationCycle = 0UL
                    var meaninglessCounter: Int
                    while (this.currentTemp.floatValue < this.targetTemp.floatValue) {
                        meaninglessCounter = 0
                        for (j in 0..iterationCycleLoops) {
                            meaninglessCounter += 1
                        }
                        currentIterationCycle += 1U
                        Log.i(logTag, "Completed iteration cycle no. $currentIterationCycle.")
                        if (Thread.currentThread().isInterrupted) {
                            throw InterruptedException()
                        }
                    }
                    Log.i(logTag, "Reached target temperature.")
                    this.stopHeatingTrigger.value = true
                } catch (e: InterruptedException) {
                    Log.i(logTag, "Interrupted. Stopping.")
                }
            }
            this.heatingThreads.add(newHeatingThread)
            newHeatingThread.start()
        }
    }

    private fun stopHeating() {
        Log.i("Defroster", "Stopping heating.")
        this.toggleHeatingButtonText.value = "Stopping..."
        this.isToggleHeatingButtonEnabled.value = false
        for (thread in this.heatingThreads) {
            thread.interrupt()
            thread.join()
        }
        this.heatingThreads.clear()
        Log.i("Defroster", "All heating threads stopped.")
        this.isHeatingStarted.value = false
        this.stopHeatingTrigger.value = false
        this.isTargetTempSliderEnabled.value = true
        this.backgroundColor.value = this.coldColor
        if (this.targetTemp.floatValue.roundToInt() > this.currentTemp.floatValue) {
            this.isToggleHeatingButtonEnabled.value = true
        }
        this.toggleHeatingButtonText.value = "Start"
    }
}
