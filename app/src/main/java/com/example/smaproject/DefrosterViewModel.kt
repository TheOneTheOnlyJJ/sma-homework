import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smaproject.HeatingState
import com.example.smaproject.HeatingStatsTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger

class DefrosterViewModel : ViewModel() {
    var currentTemp by mutableFloatStateOf(0f)
    var targetTemp by mutableIntStateOf(10)
    var heatingState by mutableStateOf(HeatingState.NOT_HEATING)
        private set
    private val heatingThreads = mutableStateListOf<Thread>()
    private val stopHeatingTrigger = MutableStateFlow(false)
    private val heatingStatsTracker: HeatingStatsTracker = HeatingStatsTracker()

    init {
        observeStopHeatingTrigger()
    }

    private fun observeStopHeatingTrigger() {
        viewModelScope.launch {
            stopHeatingTrigger.collectLatest { shouldStop ->
                if (shouldStop) {
                    stopHeating()
                    stopHeatingTrigger.update { false }
                }
            }
        }
    }

    fun toggleHeating() {
        when (this.heatingState) {
            HeatingState.NOT_HEATING -> {
                this.startHeating()
            }
            HeatingState.HEATING -> {
                this.stopHeating()
            }
            else -> {
                throw RuntimeException("Should not toggle heating while stopping heating")
            }
        }
    }

    private fun startHeating() {
        Log.i(
            "Defroster",
            "Starting heating with target temperature of ${this.targetTemp} °C."
        )
        this.heatingState = HeatingState.HEATING
        this.heatingStatsTracker.startTracking(this.currentTemp, this.targetTemp)
        val availableProcessors = Runtime.getRuntime().availableProcessors()
        Log.i("Defroster", "Device has $availableProcessors available processors.")
        for (i in 1..availableProcessors) {
            val newHeatingThread = Thread {
                val logTag = "Defroster Heating Thread No. $i"
                val iterationCycleLoops = 10_000_000
                try {
                    Log.i(logTag, "Starting.")
                    var currentIterationCycle = BigInteger.ZERO
                    var meaninglessCounter: Int
                    while (this.currentTemp < this.targetTemp) {
                        meaninglessCounter = 0
                        for (j in 0..iterationCycleLoops) {
                            meaninglessCounter += 1
                        }
                        currentIterationCycle = currentIterationCycle.add(BigInteger.ONE)
                        Log.i(logTag, "Completed iteration cycle no. $currentIterationCycle.")
                        if (Thread.currentThread().isInterrupted) {
                            throw InterruptedException()
                        }
                    }
                    Log.i(logTag, "Reached target temperature.")
                    this.stopHeatingTrigger.update { true }
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
        this.heatingState = HeatingState.STOPPING_HEATING
        for (thread in this.heatingThreads) {
            thread.interrupt()
            thread.join()
        }
        this.heatingThreads.clear()
        Log.i("Defroster", "All heating threads stopped.")
        this.heatingState = HeatingState.NOT_HEATING
        val heatingStats = this.heatingStatsTracker.stopTracking(this.currentTemp)
        Log.i("Defroster", "Heating stats: ${heatingStats}.")
    }
}
