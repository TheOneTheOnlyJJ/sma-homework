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
    val backgroundColor = mutableStateOf(coldColor)

    fun toggleDefrosting() {
        isDefrostingStarted.value = !isDefrostingStarted.value
        if (isDefrostingStarted.value) {
            backgroundColor.value = hotColor
        } else {
            backgroundColor.value = coldColor
        }
    }
}
