package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

@Composable
fun rememberMealsSettingsScreenState(
    meals: List<Meal>,
    onCreate: suspend (name: String, from: LocalTime, to: LocalTime) -> Unit,
    onUpdate: suspend (Meal) -> Unit,
    onDelete: suspend (Meal) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): MealsSettingsScreenState = rememberSaveable(
    meals,
    onCreate,
    onUpdate,
    onDelete,
    saver = Saver(
        save = {
            it.creating
        },
        restore = {
            MealsSettingsScreenState(
                meals = meals,
                initialCreating = it,
                onCreate = onCreate,
                onUpdate = onUpdate,
                onDelete = onDelete,
                coroutineScope = coroutineScope
            )
        }
    )
) {
    MealsSettingsScreenState(
        meals = meals,
        initialCreating = false,
        onCreate = onCreate,
        onUpdate = onUpdate,
        onDelete = onDelete,
        coroutineScope = coroutineScope
    )
}

enum class MealNameError {
    Empty;

    @Composable
    fun stringResource() = when (this) {
        Empty -> org.jetbrains.compose.resources.stringResource(
            Res.string.error_this_field_cannot_be_empty
        )
    }
}

@Stable
class MealsSettingsScreenState(
    meals: List<Meal>,
    initialCreating: Boolean,
    private val onCreate: suspend (name: String, from: LocalTime, to: LocalTime) -> Unit,
    private val onUpdate: suspend (Meal) -> Unit,
    private val onDelete: suspend (Meal) -> Unit,
    private val coroutineScope: CoroutineScope
) {
    var meals by mutableStateOf(meals)
        private set

    var creating by mutableStateOf(initialCreating)

    @Composable
    fun rememberMealState(meal: Meal): MealState {
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
        val isAllDayState = rememberSaveable { mutableStateOf(meal.isAllDay) }

        return rememberSaveable(
            nameInput,
            fromInput,
            toInput,
            meal,
            saver = Saver(
                save = {
                    it.isLoading
                },
                restore = {
                    MealState(
                        meal = meal,
                        initialIsLoading = it,
                        nameInput = nameInput,
                        fromInput = fromInput,
                        toInput = toInput,
                        isAllDayState = isAllDayState
                    )
                }
            )
        ) {
            MealState(
                meal = meal,
                initialIsLoading = false,
                nameInput = nameInput,
                fromInput = fromInput,
                toInput = toInput,
                isAllDayState = isAllDayState
            )
        }
    }

    @Stable
    inner class MealState(
        private val meal: Meal,
        initialIsLoading: Boolean,
        val nameInput: FormFieldWithTextFieldValue<String, MealNameError>,
        val fromInput: LocalTimeInput,
        val toInput: LocalTimeInput,
        val isAllDayState: MutableState<Boolean>
    ) {
        val dirty by derivedStateOf {
            nameInput.textFieldValue.text != meal.name ||
                fromInput.value != meal.from ||
                toInput.value != meal.to ||
                isAllDay != meal.isAllDay
        }

        val isValid by derivedStateOf {
            nameInput.error == null && !isLoading
        }

        var isLoading by mutableStateOf(initialIsLoading)
            private set

        var isAllDay: Boolean
            get() = isAllDayState.value
            set(value) {
                if (!value && fromInput.value == toInput.value) {
                    val from = fromInput.value
                    val to = LocalTime(from.hour + 2, from.minute)
                    toInput.onValueChange(to)
                }

                isAllDayState.value = value
            }

        fun onDelete() {
            coroutineScope.launch {
                isLoading = true
                onDelete(meal)
            }
        }

        fun onUpdate() {
            coroutineScope.launch {
                isLoading = true
                onUpdate(intoMeal())
            }
        }

        private fun intoMeal(): Meal {
            val to = if (isAllDay) {
                fromInput.value
            } else {
                toInput.value
            }

            return Meal(
                id = meal.id,
                name = nameInput.value,
                from = fromInput.value,
                to = to
            )
        }
    }
}
