package com.maksimowiczm.foodyou.feature.diary.ui.mealssettings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalTime

@Stable
class LocalTimeInput(initialValue: LocalTime) {
    var value: LocalTime by mutableStateOf(initialValue)
        private set

    fun onValueChange(newValue: LocalTime) {
        value = newValue
    }

    companion object {
        val Saver = Saver<LocalTimeInput, Int>(
            save = { it.value.toSecondOfDay() },
            restore = { LocalTimeInput(LocalTime.Companion.fromSecondOfDay(it)) }
        )
    }
}
