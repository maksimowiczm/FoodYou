package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.rememberFormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.stringParser
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.error_this_field_cannot_be_empty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

enum class MealNameError {
    Empty;

    @Composable
    fun stringResource() = when (this) {
        Empty -> stringResource(Res.string.error_this_field_cannot_be_empty)
    }
}

interface MealSettingsCardState {
    val nameInput: FormFieldWithTextFieldValue<String, MealNameError>
    val fromInput: LocalTimeInput
    val toInput: LocalTimeInput
    val isAllDay: Boolean

    val isDirty: Boolean
    val isValid: Boolean
    val isLoading: Boolean

    fun setIsAllDay(value: Boolean)
    fun update()
    fun delete()
}

private class MealSettingsCardStateImpl(
    initialMeal: Meal,
    override val nameInput: FormFieldWithTextFieldValue<String, MealNameError>,
    override val fromInput: LocalTimeInput,
    override val toInput: LocalTimeInput,
    private val isAllDayState: MutableState<Boolean>,
    private val isLoadingState: MutableState<Boolean>,
    private val onUpdate: suspend (Meal) -> Unit,
    private val onDelete: suspend (Meal) -> Unit,
    private val coroutineScope: CoroutineScope
) : MealSettingsCardState {
    private var meal by mutableStateOf(initialMeal)

    override val isDirty: Boolean by derivedStateOf {
        if (meal.isAllDay) {
            isAllDay == false || meal.name != nameInput.value
        } else {
            meal.from != fromInput.value || meal.to != toInput.value || meal.name != nameInput.value
        }
    }

    override val isValid: Boolean by derivedStateOf {
        nameInput.error == null
    }

    override val isLoading by isLoadingState

    override val isAllDay: Boolean by derivedStateOf { isAllDayState.value }

    override fun setIsAllDay(value: Boolean) {
        isAllDayState.value = value
    }

    override fun update() {
        val to = if (isAllDay) {
            fromInput.value
        } else {
            toInput.value
        }

        val meal = meal.copy(
            name = nameInput.value,
            from = fromInput.value,
            to = to
        )

        coroutineScope.launch {
            isLoadingState.value = true

            onUpdate(meal)
            this@MealSettingsCardStateImpl.meal = meal

            isLoadingState.value = false
        }
    }

    override fun delete() {
        coroutineScope.launch {
            isLoadingState.value = true
            onDelete(meal)
            isLoadingState.value = false
        }
    }
}

@Composable
fun rememberMealSettingsCardState(
    meal: Meal,
    onUpdate: suspend (Meal) -> Unit,
    onDelete: suspend (Meal) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): MealSettingsCardState {
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
    val isAllDay = rememberSaveable { mutableStateOf(meal.isAllDay) }
    val isLoading = rememberSaveable { mutableStateOf(false) }

    return remember(nameInput, fromInput, toInput, meal) {
        MealSettingsCardStateImpl(
            initialMeal = meal,
            nameInput = nameInput,
            fromInput = fromInput,
            toInput = toInput,
            isAllDayState = isAllDay,
            isLoadingState = isLoading,
            onUpdate = onUpdate,
            onDelete = onDelete,
            coroutineScope = coroutineScope
        )
    }
}
