package com.maksimowiczm.foodyou.core.feature.diary.ui.mealssettings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalTime

@Composable
fun rememberLocalTimeInput(
    initialValue: LocalTime,
    initialDirty: Boolean = false
): LocalTimeInput {
    return rememberSaveable(
        initialValue,
        initialDirty,
        saver = Saver(
            save = {
                it.value.toSecondOfDay()
            },
            restore = {
                LocalTimeInput(
                    initialValue = LocalTime.fromSecondOfDay(it),
                    initialDirty = initialDirty
                )
            }
        )
    ) {
        LocalTimeInput(
            initialValue = initialValue,
            initialDirty = initialDirty
        )
    }
}

@Stable
class LocalTimeInput(
    initialValue: LocalTime,
    initialDirty: Boolean
) {
    var value: LocalTime by mutableStateOf(initialValue)
        private set

    var dirty: Boolean by mutableStateOf(initialDirty)
        private set

    fun onValueChange(newValue: LocalTime) {
        value = newValue
        dirty = true
    }
}
