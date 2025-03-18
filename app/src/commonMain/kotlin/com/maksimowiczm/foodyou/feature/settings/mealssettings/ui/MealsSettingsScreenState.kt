package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.data.model.Meal

@Composable
fun rememberMealsSettingsScreenState(meals: List<Meal>) = rememberSaveable(
    meals,
    saver = Saver(
        save = {
            arrayListOf(
                it.isCreating,
                it.isReordering
            )
        },
        restore = {
            MealsSettingsScreenState(
                meals = meals,
                isCreating = it[0],
                isReordering = it[1]
            )
        }
    )
) {
    MealsSettingsScreenState(
        meals = meals,
        isCreating = false,
        isReordering = false
    )
}

@Stable
class MealsSettingsScreenState(meals: List<Meal>, isCreating: Boolean, isReordering: Boolean) {
    var isCreating by mutableStateOf(isCreating)
    var isReordering by mutableStateOf(isReordering)
    var meals by mutableStateOf(meals)
}
