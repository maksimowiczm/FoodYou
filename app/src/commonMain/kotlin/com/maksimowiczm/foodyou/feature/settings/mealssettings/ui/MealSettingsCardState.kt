package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.rememberFormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.stringParser
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

enum class MealNameError {
    Empty;

    @Composable
    fun stringResource() = when (this) {
        Empty -> stringResource(Res.string.error_this_field_cannot_be_empty)
    }
}

@Composable
fun rememberMealsSettingsCardState(meal: Meal): MealSettingsCardState {
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
    val fromInput = rememberLocalTimeInput(
        initialValue = meal.from
    )
    val toInput = rememberLocalTimeInput(
        initialValue = meal.to
    )
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
                MealSettingsCardState(
                    initialIsLoading = it,
                    initialName = meal.name,
                    initialFrom = meal.from,
                    initialTo = meal.to,
                    initialIsAllDay = meal.isAllDay,
                    nameInput = nameInput,
                    fromInput = fromInput,
                    toInput = toInput,
                    isAllDayState = isAllDayState
                )
            }
        )
    ) {
        MealSettingsCardState(
            initialIsLoading = false,
            initialName = meal.name,
            initialFrom = meal.from,
            initialTo = meal.to,
            initialIsAllDay = meal.isAllDay,
            nameInput = nameInput,
            fromInput = fromInput,
            toInput = toInput,
            isAllDayState = isAllDayState
        )
    }
}

@Composable
fun rememberMealsSettingsCardState(
    initialName: String? = null,
    initialFrom: LocalTime? = null,
    initialTo: LocalTime? = null,
    nameInput: FormFieldWithTextFieldValue<String, MealNameError> =
        rememberFormFieldWithTextFieldValue(
            initialTextFieldValue = TextFieldValue(
                text = initialName ?: "",
                selection = TextRange(initialName?.length ?: 0)
            ),
            initialValue = initialName ?: "",
            parser = stringParser(
                onEmpty = { MealNameError.Empty }
            )
        ),
    fromInput: LocalTimeInput = rememberLocalTimeInput(
        initialFrom ?: Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time
            .trimSeconds()
    ),
    toInput: LocalTimeInput = rememberLocalTimeInput(
        initialTo ?: Clock.System.now()
            .plus(2.hours)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time
            .trimSeconds()
    )
): MealSettingsCardState {
    val isAllDayState = rememberSaveable {
        mutableStateOf(fromInput.value == toInput.value)
    }

    return rememberSaveable(
        nameInput,
        fromInput,
        toInput,
        saver = Saver(
            save = {
                it.isLoading
            },
            restore = {
                MealSettingsCardState(
                    initialIsLoading = it,
                    initialName = initialName,
                    initialFrom = initialFrom,
                    initialTo = initialTo,
                    initialIsAllDay = initialFrom == initialTo,
                    nameInput = nameInput,
                    fromInput = fromInput,
                    toInput = toInput,
                    isAllDayState = isAllDayState
                )
            }
        )
    ) {
        MealSettingsCardState(
            initialIsLoading = false,
            initialName = initialName,
            initialFrom = initialFrom,
            initialTo = initialTo,
            initialIsAllDay = initialFrom == initialTo,
            nameInput = nameInput,
            fromInput = fromInput,
            toInput = toInput,
            isAllDayState = isAllDayState
        )
    }
}

@Stable
class MealSettingsCardState(
    initialIsLoading: Boolean,
    private val initialName: String?,
    private val initialFrom: LocalTime?,
    private val initialTo: LocalTime?,
    private val initialIsAllDay: Boolean,
    val nameInput: FormFieldWithTextFieldValue<String, MealNameError>,
    val fromInput: LocalTimeInput,
    val toInput: LocalTimeInput,
    val isAllDayState: MutableState<Boolean>
) {
    val dirty by derivedStateOf {
        nameInput.textFieldValue.text != initialName ||
            fromInput.value != initialFrom ||
            toInput.value != initialTo ||
            isAllDay != initialIsAllDay
    }

    val isValid by derivedStateOf {
        nameInput.error == null && !isLoading
    }

    var isLoading by mutableStateOf(initialIsLoading)

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

    fun intoMeal(id: Long): Meal {
        val to = if (isAllDay) {
            fromInput.value
        } else {
            toInput.value
        }

        return Meal(
            id = id,
            name = nameInput.value,
            from = fromInput.value,
            to = to
        )
    }
}

private fun LocalTime.trimSeconds() = LocalTime(hour, minute)
