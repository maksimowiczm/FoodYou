package com.maksimowiczm.foodyou.feature.diary.ui.mealssettings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.TextFieldValue
import com.maksimowiczm.foodyou.feature.diary.data.model.Meal

@Stable
class MealCardStateWithMeal(
    val meal: Meal,
    override val nameInput: MutableState<TextFieldValue>,
    override val fromTimeInput: LocalTimeInput,
    override val toTimeInput: LocalTimeInput,
    override val isAllDay: MutableState<Boolean>
) : MealCardState {
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
