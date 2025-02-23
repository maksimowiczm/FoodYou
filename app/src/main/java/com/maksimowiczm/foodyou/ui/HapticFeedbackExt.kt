package com.maksimowiczm.foodyou.ui

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun HapticFeedback.performToggle(state: Boolean) {
    val type = if (state) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
    performHapticFeedback(type)
}
