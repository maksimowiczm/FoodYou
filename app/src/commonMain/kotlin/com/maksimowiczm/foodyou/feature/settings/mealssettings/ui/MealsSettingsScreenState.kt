package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.data.model.Meal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalTime

@Composable
fun rememberMealsSettingsScreenState(
    meals: List<Meal>,
    onCreate: suspend (name: String, from: LocalTime, to: LocalTime) -> Unit,
    onUpdate: suspend (Meal) -> Unit,
    onDelete: suspend (Meal) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): MealsSettingsScreenState = rememberSaveable(
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

@Stable
class MealsSettingsScreenState(
    meals: List<Meal>,
    initialCreating: Boolean,
    val onCreate: suspend (name: String, from: LocalTime, to: LocalTime) -> Unit,
    val onUpdate: suspend (Meal) -> Unit,
    val onDelete: suspend (Meal) -> Unit,
    val coroutineScope: CoroutineScope
) {
    var meals by mutableStateOf(meals)
        private set

    var creating by mutableStateOf(initialCreating)
}
