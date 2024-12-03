import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class DefrostViewModel : ViewModel() {
    private val coldColor = Color(0xffdcf3ff)
    private val hotColor = Color(0xfffde0e0)

    val currentTemp = mutableFloatStateOf(0f)
    val targetTemp = mutableFloatStateOf(10f)
    val isDefrostingStarted = mutableStateOf(false)
    val isTargetTempSliderEnabled = mutableStateOf(true)
    val backgroundColor = mutableStateOf(coldColor)

    fun toggleDefrosting() {
        this.isDefrostingStarted.value = !isDefrostingStarted.value
        if (this.isDefrostingStarted.value) {
            this.isTargetTempSliderEnabled.value = false
            this.backgroundColor.value = this.hotColor
        } else {
            this.isTargetTempSliderEnabled.value = true
            this.backgroundColor.value = this.coldColor
        }
    }
}
