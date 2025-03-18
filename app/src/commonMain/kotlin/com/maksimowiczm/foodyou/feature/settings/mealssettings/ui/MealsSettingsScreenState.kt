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
fun rememberMealsSettingsScreenState(meals: List<Meal>): MealsSettingsScreenState {
    val mealsWithState = meals
        .sortedBy { it.id }
        .map { it to rememberMealSettingsCardState(it) }

    return rememberSaveable(
        mealsWithState,
        saver = Saver(
            save = {
                arrayListOf(
                    it.isCreating,
                    it.isReordering
                )
            },
            restore = {
                MealsSettingsScreenState(
                    initialMeals = mealsWithState,
                    isCreating = it[0],
                    isReordering = it[1]
                )
            }
        )
    ) {
        MealsSettingsScreenState(
            initialMeals = mealsWithState,
            isCreating = false,
            isReordering = false
        )
    }
}

@Stable
class MealsSettingsScreenState(
    initialMeals: List<Pair<Meal, MealSettingsCardStateImpl>>,
    isCreating: Boolean,
    isReordering: Boolean
) {
    var isCreating by mutableStateOf(isCreating)
    var isReordering by mutableStateOf(isReordering)
    var meals by mutableStateOf(initialMeals)
        private set

    fun updateMeals(newMeals: List<Meal>) {
        val newOrder = meals
            .mapNotNull { old ->
                newMeals.find { it.id == old.first.id }?.let { it to old.second }
            }

        meals = newOrder
    }
}
