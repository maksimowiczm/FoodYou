package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.LocalTimeInput
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.MealNameError
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.rememberLocalTimeInput
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.rememberFormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.stringParser

interface MealSettingsCardState {
    val nameInput: FormFieldWithTextFieldValue<String, MealNameError>
    val fromInput: LocalTimeInput
    val toInput: LocalTimeInput

    val isDirty: Boolean
    val isValid: Boolean
    val isAllDay: Boolean
    fun setIsAllDay(value: Boolean)

    fun toMeal(): Meal
}

@Composable
fun rememberMealSettingsCardState(meal: Meal): MealSettingsCardState {
    val nameInput = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(
            text = meal.name,
            selection = TextRange(meal.name.length)
        ),
        initialValue = meal.name,
        parser = stringParser(
            onEmpty = { MealNameError.Empty }
        )
    )
    val fromInput = rememberLocalTimeInput(meal.from)
    val toInput = rememberLocalTimeInput(meal.to)

    return remember(meal, nameInput, fromInput, toInput) {
        MealSettingsCardStateImpl(
            meal = meal,
            nameInput = nameInput,
            fromInput = fromInput,
            toInput = toInput
        )
    }
}

@Stable
private class MealSettingsCardStateImpl(
    private val meal: Meal,
    override val nameInput: FormFieldWithTextFieldValue<String, MealNameError>,
    override val fromInput: LocalTimeInput,
    override val toInput: LocalTimeInput,
    initialIsAllDay: Boolean = meal.isAllDay
) : MealSettingsCardState {
    override val isDirty: Boolean by derivedStateOf {
        val input =
            nameInput.value != meal.name || fromInput.value != meal.from || toInput.value != meal.to

        if (meal.from == meal.to) {
            input
        } else {
            input || isAllDay != meal.isAllDay
        }
    }

    override val isValid: Boolean by derivedStateOf { nameInput.error == null }

    override var isAllDay: Boolean by mutableStateOf(initialIsAllDay)
        private set

    override fun setIsAllDay(value: Boolean) {
        isAllDay = value
    }

    override fun toMeal(): Meal {
        val to = if (isAllDay) {
            fromInput.value
        } else {
            toInput.value
        }

        return meal.copy(
            name = nameInput.value,
            from = fromInput.value,
            to = to
        )
    }
}
