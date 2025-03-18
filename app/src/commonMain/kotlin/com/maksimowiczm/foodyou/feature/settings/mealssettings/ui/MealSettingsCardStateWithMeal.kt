package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.TextFieldValue
import com.maksimowiczm.foodyou.data.model.Meal
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface MealSettingsCardState {
    val nameInput: MutableState<TextFieldValue>
    val fromTimeInput: LocalTimeInput
    val toTimeInput: LocalTimeInput
    val isAllDay: MutableState<Boolean>
    val isDirty: Boolean
}

@Stable
class MealSettingsCardStateWithMeal(
    val meal: Meal,
    override val nameInput: MutableState<TextFieldValue>,
    override val fromTimeInput: LocalTimeInput,
    override val toTimeInput: LocalTimeInput,
    override val isAllDay: MutableState<Boolean>
) : MealSettingsCardState {
    private val name by derivedStateOf { nameInput.value.text }
    private val fromTime by derivedStateOf { fromTimeInput.value }
    private val toTime by derivedStateOf { toTimeInput.value }

    override val isDirty by derivedStateOf {
        val input = name != meal.name || fromTime != meal.from || toTime != meal.to

        // If the meal is all day meal ignore the all day switch state
        if (meal.from == meal.to) {
            input
        } else {
            input || isAllDay.value != meal.isAllDay
        }
    }

    fun intoMeal(): Meal {
        val toTime = if (isAllDay.value) {
            fromTime
        } else {
            toTime
        }

        return Meal(
            id = meal.id,
            name = name,
            from = fromTime,
            to = toTime,
            rank = meal.rank
        )
    }
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

@Stable
class MealSettingsCardStateWithoutMeal(
    override val nameInput: MutableState<TextFieldValue>,
    override val fromTimeInput: LocalTimeInput,
    override val toTimeInput: LocalTimeInput,
    override val isAllDay: MutableState<Boolean>
) : MealSettingsCardState {
    override val isDirty by derivedStateOf {
        nameInput.value.text.isNotEmpty()
    }
}
