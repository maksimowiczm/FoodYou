package com.maksimowiczm.foodyou.feature.food.diary.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.fooddiary.domain.repository.MealRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class MealSettingsViewModel(private val mealRepository: MealRepository) : ViewModel() {

    val meals: StateFlow<List<MealModel>?> =
        mealRepository
            .observeMeals()
            .distinctUntilChanged()
            .map { meals ->
                meals
                    .sortedBy { it.rank }
                    .map { meal ->
                        MealModel(
                            id = meal.id,
                            name = meal.name,
                            from = meal.from,
                            to = meal.to,
                            isAllDay = meal.from == meal.to,
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun deleteMeal(mealModel: MealModel) {
        viewModelScope.launch { mealRepository.deleteMeal(mealModel.id) }
    }

    fun updateMeal(mealModel: MealModel) {
        viewModelScope.launch {
            mealRepository.updateMeal(
                id = mealModel.id,
                name = mealModel.name,
                from = mealModel.from,
                to = mealModel.to,
            )
        }
    }

    fun createMeal(mealModel: MealModel) {
        viewModelScope.launch {
            mealRepository.insertMealWithLastRank(
                name = mealModel.name,
                from = mealModel.from,
                to = mealModel.to,
            )
        }
    }

    fun updateMealOrder(mealModels: List<MealModel>) {
        viewModelScope.launch { mealRepository.reorderMeals(mealModels.map { it.id }) }
    }
}
