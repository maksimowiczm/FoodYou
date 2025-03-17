package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.runtime.Composable
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

    val isLoading: Boolean
    val isDirty: Boolean
    val isValid: Boolean
    val isAllDay: Boolean
    fun setIsAllDay(value: Boolean)
}

@Composable
fun rememberMealSettingsCardState(meal: Meal, isLoading: Boolean): MealSettingsCardState {
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

    return remember {
        MealSettingsCardStateImpl(
            meal = meal,
            nameInput = nameInput,
            fromInput = fromInput,
            toInput = toInput,
            isLoading = isLoading
        )
    }
}

private class MealSettingsCardStateImpl(
    private val meal: Meal,
    override val nameInput: FormFieldWithTextFieldValue<String, MealNameError>,
    override val fromInput: LocalTimeInput,
    override val toInput: LocalTimeInput,
    override val isLoading: Boolean,
    initialIsAllDay: Boolean = meal.isAllDay
) : MealSettingsCardState {
    override val isDirty: Boolean by derivedStateOf {
        nameInput.value != meal.name ||
            fromInput.value != meal.from ||
            toInput.value != meal.to ||
            isAllDay != meal.isAllDay
    }

    override val isValid: Boolean by mutableStateOf(false)

    override var isAllDay: Boolean by mutableStateOf(initialIsAllDay)
        private set

    override fun setIsAllDay(value: Boolean) {
        isAllDay = value
    }
}
