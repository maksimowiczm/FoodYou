package com.maksimowiczm.foodyou.feature.diary.mealssettings.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Stable
internal class MealCardStateWithoutMeal(
    override val nameInput: TextFieldState,
    override val fromTimeInput: LocalTimeInput,
    override val toTimeInput: LocalTimeInput,
    override val isAllDay: MutableState<Boolean>
) : MealCardState {
    override val isDirty = true
}

@Composable
internal fun rememberMealCardState(): MealCardState {
    val nameInput = rememberTextFieldState()

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
        MealCardStateWithoutMeal(
            nameInput = nameInput,
            fromTimeInput = fromTimeInput,
            toTimeInput = toTimeInput,
            isAllDay = isAllDay
        )
    }
}
