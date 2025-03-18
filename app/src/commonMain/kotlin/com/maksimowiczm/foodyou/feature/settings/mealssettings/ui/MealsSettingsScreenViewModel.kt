package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.model.Meal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MealsSettingsScreenViewModel(private val diaryRepository: DiaryRepository) : ViewModel() {
    private fun DiaryRepository.observeSortedMeals() =
        observeMeals().map { it.sortedBy { it.rank } }

    val meals = diaryRepository.observeSortedMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { diaryRepository.observeSortedMeals().first() }
        )

    // TODO add item
    fun orderMeals(meals: List<Meal>) {
        viewModelScope.launch {
            val map = meals.mapIndexed { index, meal -> meal.id to index }.toMap()
            diaryRepository.orderMeals(map)
        }
    }
}
