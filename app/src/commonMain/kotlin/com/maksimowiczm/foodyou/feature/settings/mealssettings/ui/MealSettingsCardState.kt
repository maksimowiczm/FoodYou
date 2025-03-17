package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.runtime.Composable
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
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.error_this_field_cannot_be_empty
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

interface MealSettingsCardState {
    val nameInput: FormFieldWithTextFieldValue<String, MealNameError>
    val fromInput: LocalTimeInput
    val toInput: LocalTimeInput

    val isDirty: Boolean
    val isValid: Boolean
    val isAllDay: Boolean
    fun setIsAllDay(value: Boolean)
}

@Composable
fun rememberMealSettingsCardState(meal: Meal): MealSettingsCardStateImpl {
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

    return rememberSaveable(
        meal,
        nameInput,
        fromInput,
        toInput,
        saver = Saver(
            save = { it.isAllDay },
            restore = {
                MealSettingsCardStateImpl(
                    meal = meal,
                    nameInput = nameInput,
                    fromInput = fromInput,
                    toInput = toInput,
                    initialIsAllDay = it
                )
            }
        )
    ) {
        MealSettingsCardStateImpl(
            meal = meal,
            nameInput = nameInput,
            fromInput = fromInput,
            toInput = toInput
        )
    }
}

@Stable
class MealSettingsCardStateImpl(
    private val meal: Meal,
    override val nameInput: FormFieldWithTextFieldValue<String, MealNameError>,
    override val fromInput: LocalTimeInput,
    override val toInput: LocalTimeInput,
    initialIsAllDay: Boolean = meal.isAllDay
) : MealSettingsCardState {
    override val isDirty: Boolean by derivedStateOf {
        val input =
            nameInput.value != meal.name || fromInput.value != meal.from || toInput.value != meal.to

        // If the meal is all day meal ignore the all day switch state
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

    fun toMeal(): Meal {
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

@Composable
fun rememberMealSettingsCardState(): NoMealSettingsCardStateImpl {
    val nameInput = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(),
        initialValue = "",
        parser = stringParser(
            onEmpty = { MealNameError.Empty }
        )
    )

    val fromInput = rememberLocalTimeInput(
        Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time
            .trimSeconds()
    )

    val toInput = rememberLocalTimeInput(
        Clock.System.now()
            .plus(2.hours)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time
            .trimSeconds()
    )

    return rememberSaveable(
        nameInput,
        fromInput,
        toInput,
        saver = Saver(
            save = { it.isAllDay },
            restore = { NoMealSettingsCardStateImpl(nameInput, fromInput, toInput) }
        )
    ) {
        NoMealSettingsCardStateImpl(
            nameInput = nameInput,
            fromInput = fromInput,
            toInput = toInput
        )
    }
}

@Stable
class NoMealSettingsCardStateImpl(
    override val nameInput: FormFieldWithTextFieldValue<String, MealNameError>,
    override val fromInput: LocalTimeInput,
    override val toInput: LocalTimeInput
) : MealSettingsCardState {
    override val isDirty: Boolean by derivedStateOf { nameInput.value.isNotEmpty() }
    override val isValid: Boolean by derivedStateOf { nameInput.error == null }
    override var isAllDay: Boolean by mutableStateOf(false)

    override fun setIsAllDay(value: Boolean) {
        isAllDay = value
    }
}

private fun LocalTime.trimSeconds() = LocalTime(hour, minute)
