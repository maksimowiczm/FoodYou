package com.maksimowiczm.foodyou.app.ui.common.extension

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun HapticFeedback.toggle(state: Boolean) {
    val type = if (state) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
    performHapticFeedback(type)
}

fun HapticFeedback.segmentFrequentTick() {
    performHapticFeedback(HapticFeedbackType.TextHandleMove)
}

fun HapticFeedback.confirm() {
    performHapticFeedback(HapticFeedbackType.Confirm)
}
