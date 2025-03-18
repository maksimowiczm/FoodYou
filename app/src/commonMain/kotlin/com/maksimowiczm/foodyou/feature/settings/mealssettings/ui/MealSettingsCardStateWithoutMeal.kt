package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Stable
class MealSettingsCardStateWithoutMeal(
    override val nameInput: MutableState<TextFieldValue>,
    override val fromTimeInput: LocalTimeInput,
    override val toTimeInput: LocalTimeInput,
    override val isAllDay: MutableState<Boolean>
) : MealSettingsCardState {
    override val isDirty = true
}

@Composable
fun rememberMealSettingsCardState(): MealSettingsCardState {
    val nameInput = rememberSaveable(
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue())
    }

    val from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val to = Clock.System.now().plus(2.hours).toLocalDateTime(TimeZone.currentSystemDefault())

    val fromTimeInput = rememberSaveable(
        saver = LocalTimeInput.Saver
    ) {
        LocalTimeInput(from.time)
    }

    val toTimeInput = rememberSaveable(
        saver = LocalTimeInput.Saver
    ) {
        LocalTimeInput(to.time)
    }

    val isAllDay = rememberSaveable {
        mutableStateOf(false)
    }

    return remember {
        MealSettingsCardStateWithoutMeal(
            nameInput = nameInput,
            fromTimeInput = fromTimeInput,
            toTimeInput = toTimeInput,
            isAllDay = isAllDay
        )
    }
}
