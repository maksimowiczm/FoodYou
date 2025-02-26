package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalTime

@Composable
fun rememberLocalTimeInput(initialValue: LocalTime): LocalTimeInput = rememberSaveable(
    initialValue,
    saver = Saver(
        save = {
            it.value.toSecondOfDay()
        },
        restore = {
            LocalTimeInput(
                initialValue = LocalTime.fromSecondOfDay(it)
            )
        }
    )
) {
    LocalTimeInput(
        initialValue = initialValue
    )
}

@Stable
class LocalTimeInput(initialValue: LocalTime) {
    var value: LocalTime by mutableStateOf(initialValue)
        private set

    fun onValueChange(newValue: LocalTime) {
        value = newValue
    }
}
