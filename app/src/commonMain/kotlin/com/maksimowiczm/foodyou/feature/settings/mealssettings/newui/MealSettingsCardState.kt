package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.TextFieldValue
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.LocalTimeInput

@Stable
class MealSettingsCardState(
    val meal: Meal,
    val nameInput: MutableState<TextFieldValue>,
    val fromTimeInput: LocalTimeInput,
    val toTimeInput: LocalTimeInput,
    val isAllDay: MutableState<Boolean>
) {
    private val name by derivedStateOf { nameInput.value.text }
    private val fromTime by derivedStateOf { fromTimeInput.value }
    private val toTime by derivedStateOf { toTimeInput.value }

    val isDirty by derivedStateOf {
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
