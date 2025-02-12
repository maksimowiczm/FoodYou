package com.maksimowiczm.foodyou.core.ui

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun HapticFeedback.toggle(state: Boolean) = if (state) {
    performHapticFeedback(HapticFeedbackType.ToggleOn)
} else {
    performHapticFeedback(HapticFeedbackType.ToggleOff)
}
