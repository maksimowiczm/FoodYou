package com.maksimowiczm.foodyou.feature.food.diary.meal

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.app.ui.shared.extension.now
import com.maksimowiczm.foodyou.shared.compose.extension.Saver
import com.maksimowiczm.foodyou.shared.compose.form.FormField
import com.maksimowiczm.foodyou.shared.compose.form.nonBlankStringValidator
import com.maksimowiczm.foodyou.shared.compose.form.rememberFormField
import com.maksimowiczm.foodyou.shared.compose.form.stringParser
import kotlinx.datetime.LocalTime

@Composable
internal fun rememberMealCardState(meal: MealModel?): MealCardState {
    val name =
        rememberFormField(
            initialValue = meal?.name ?: "",
            parser = stringParser(),
            validator = nonBlankStringValidator(onEmpty = { "Meal name cannot be empty" }),
            textFieldState = rememberTextFieldState(meal?.name ?: ""),
        )

    val now = LocalTime.now()
    val fromTime =
        rememberSaveable(meal, stateSaver = LocalTime.Companion.Saver) {
            mutableStateOf(meal?.from ?: now)
        }

    val toTime =
        rememberSaveable(meal, stateSaver = LocalTime.Companion.Saver) {
            mutableStateOf(meal?.to ?: LocalTime(hour = (now.hour + 1) % 24, minute = now.minute))
        }

    val isAllDay = rememberSaveable(meal) { mutableStateOf(meal?.isAllDay ?: false) }

    val isModified =
        remember(name, fromTime, toTime) {
            derivedStateOf {
                if (meal != null) {
                    val input =
                        name.value != meal.name ||
                            fromTime.value != meal.from ||
                            toTime.value != meal.to

                    if (meal.isAllDay) {
                        input
                    } else {
                        input || isAllDay.value != meal.isAllDay
                    }
                } else {
                    name.value.isNotEmpty()
                }
            }
        }

    return remember(name, fromTime, toTime, isModified, isAllDay) {
        MealCardState(
            name = name,
            fromTime = fromTime,
            toTime = toTime,
            isAllDay = isAllDay,
            isModified = isModified,
        )
    }
}

internal class MealCardState(
    val name: FormField<String, String>,
    fromTime: MutableState<LocalTime>,
    toTime: MutableState<LocalTime>,
    isModified: State<Boolean>,
    isAllDay: MutableState<Boolean>,
) {
    var fromTime by fromTime
    var toTime by toTime
    val isModified by isModified
    var isAllDay by isAllDay

    val isValid by derivedStateOf { name.error == null }

    fun intoMealModel(id: Long): MealModel =
        MealModel(
            id = id,
            name = name.value,
            from = fromTime,
            to = if (isAllDay) fromTime else toTime,
            isAllDay = isAllDay,
        )
}
