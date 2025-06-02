package com.maksimowiczm.foodyou.feature.meal.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.repository.MealRepository
import com.maksimowiczm.foodyou.core.model.Meal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

internal class MealsSettingsScreenViewModel(private val mealRepository: MealRepository) :
    ViewModel() {
    val sortedMeals = mealRepository
        .observeMeals()
        .map { list -> list.sortedBy { it.rank } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )

    fun updateMealsRanks(meals: List<Meal>) {
        viewModelScope.launch {
            val map = meals.mapIndexed { index, meal -> meal.id to index }.toMap()
            mealRepository.updateMealsRanks(map)
        }
    }

    fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.updateMeal(meal.id, meal.name, meal.from, meal.to)
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.deleteMeal(meal.id)
        }
    }

    fun createMeal(name: String, from: LocalTime, to: LocalTime) {
        viewModelScope.launch {
            mealRepository.createMeal(
                name = name,
                from = from,
                to = to
            )
        }
    }
}
