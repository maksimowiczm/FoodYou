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
fun rememberMealsSettingsScreenState(initialMeals: List<Meal>) = rememberSaveable(
    saver = Saver(
        save = {
            arrayListOf(
                it.isCreating,
                it.isReordering
            )
        },
        restore = {
            MealsSettingsScreenState(
                initialMeals = initialMeals,
                isCreating = it[0],
                isReordering = it[1]
            )
        }
    )
) {
    MealsSettingsScreenState(
        initialMeals = initialMeals,
        isCreating = false,
        isReordering = false
    )
}

@Stable
class MealsSettingsScreenState(
    initialMeals: List<Meal>,
    isCreating: Boolean,
    isReordering: Boolean
) {
    var isCreating by mutableStateOf(isCreating)
    var isReordering by mutableStateOf(isReordering)

    var meals by mutableStateOf(initialMeals)
        private set

    fun updateRanks(newMeals: List<Meal>) {
        this.meals = newMeals.mapIndexedNotNull { i, meal ->
            val oldMeal = this.meals.find { it.id == meal.id }

            if (oldMeal == null) {
                return@mapIndexedNotNull null
            }

            oldMeal.copy(
                rank = i
            )
        }
    }
}
