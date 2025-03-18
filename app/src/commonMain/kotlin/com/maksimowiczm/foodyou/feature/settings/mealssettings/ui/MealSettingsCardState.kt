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
import kotlinx.datetime.LocalTime

interface MealSettingsCardState {
    val nameInput: MutableState<TextFieldValue>
    val fromInput: MutableState<LocalTimeInput>
    val toInput: MutableState<LocalTimeInput>

    val isDirty: Boolean
    val isValid: Boolean
    val isAllDay: Boolean
    fun setIsAllDay(value: Boolean)
}

@Composable
fun rememberMealSettingsCardState(meal: Meal): MealSettingsCardStateImpl = rememberSaveable(
    meal,
    saver = Saver(
        save = { state ->
            listOf(
                state.isAllDay,
                with(TextFieldValue.Saver) { save(state.nameInput.value) },
                with(LocalTimeInput.Saver) { save(state.fromInput.value) },
                with(LocalTimeInput.Saver) { save(state.toInput.value) }
            )
        },
        restore = { (isAllDay, nameInput, fromInput, toInput) ->
            MealSettingsCardStateImpl(
                meal = meal,
                nameInput = mutableStateOf(
                    with(TextFieldValue.Saver) {
                        restore(nameInput!!)!!
                    }
                ),
                fromInput = mutableStateOf(
                    with(LocalTimeInput.Saver) {
                        restore(fromInput as Int)!!
                    }
                ),
                toInput = mutableStateOf(
                    with(LocalTimeInput.Saver) {
                        restore(toInput as Int)!!
                    }
                ),
                initialIsAllDay = isAllDay as Boolean
            )
        }
    )
) {
    MealSettingsCardStateImpl(
        meal = meal,
        nameInput = mutableStateOf(TextFieldValue(meal.name, TextRange(meal.name.length))),
        fromInput = mutableStateOf(LocalTimeInput(meal.from)),
        toInput = mutableStateOf(LocalTimeInput(meal.to)),
        initialIsAllDay = meal.isAllDay
    )
}

@Stable
class MealSettingsCardStateImpl(
    private val meal: Meal,
    override val nameInput: MutableState<TextFieldValue>,
    override val fromInput: MutableState<LocalTimeInput>,
    override val toInput: MutableState<LocalTimeInput>,
    initialIsAllDay: Boolean = meal.isAllDay
) : MealSettingsCardState {
    override val isDirty: Boolean by derivedStateOf {
        val input =
            nameInput.value.text != meal.name ||
                fromInput.value.value != meal.from ||
                toInput.value.value != meal.to

        // If the meal is all day meal ignore the all day switch state
        if (meal.from == meal.to) {
            input
        } else {
            input || isAllDay != meal.isAllDay
        }
    }

    override val isValid: Boolean by derivedStateOf { nameInput.value.text.isNotEmpty() }

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
            name = nameInput.value.text,
            from = fromInput.value.value,
            to = to.value
        )
    }
}

// @Composable
// fun rememberMealSettingsCardState(): NoMealSettingsCardStateImpl {
//    return rememberSaveable(
//        saver = Saver(
//            save = { state ->
//                listOf(
//                    state.isAllDay,
//                    with(TextFieldValue.Saver) { save(state.nameInput) },
//                    with(LocalTimeInput.Saver) { save(state.fromInput) },
//                    with(LocalTimeInput.Saver) { save(state.toInput) }
//                )
//            },
//            restore = { (isAllDay, nameInput, fromInput, toInput) ->
//                NoMealSettingsCardStateImpl(
//                    nameInput = with(TextFieldValue.Saver) { restore(nameInput!!)!! },
//                    fromInput = with(LocalTimeInput.Saver) { restore(fromInput as Int)!! },
//                    toInput = with(LocalTimeInput.Saver) { restore(toInput as Int)!! },
//                    initialIsAllDay = isAllDay as Boolean
//                )
//            }
//        )
//    ) {
//        NoMealSettingsCardStateImpl(
//            nameInput = TextFieldValue(),
//            fromInput = LocalTimeInput(LocalTime(12, 0)),
//            toInput = LocalTimeInput(LocalTime(13, 0)),
//            initialIsAllDay = false
//        )
//    }
// }

// @Stable
// class NoMealSettingsCardStateImpl(
//    override val nameInput: TextFieldValue,
//    override val fromInput: LocalTimeInput,
//    override val toInput: LocalTimeInput,
//    initialIsAllDay: Boolean
// ) : MealSettingsCardState {
//    override val isDirty: Boolean by derivedStateOf { nameInput.text.isNotEmpty() }
//    override val isValid: Boolean by derivedStateOf { nameInput.text.isNotEmpty() }
//    override var isAllDay: Boolean by mutableStateOf(initialIsAllDay)
//
//    override fun setIsAllDay(value: Boolean) {
//        isAllDay = value
//    }
// }

private fun LocalTime.trimSeconds() = LocalTime(hour, minute)
