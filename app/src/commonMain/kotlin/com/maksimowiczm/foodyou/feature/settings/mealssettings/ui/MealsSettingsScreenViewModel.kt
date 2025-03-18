package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.data.model.Meal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalTime

class MealsSettingsScreenViewModel(
    private val diaryRepository: DiaryRepository,
    private val stringFormatRepository: StringFormatRepository
) : ViewModel() {
    private fun DiaryRepository.observeSortedMeals() =
        observeMeals().map { it.sortedBy { it.rank } }

    val sortedMeals = diaryRepository.observeSortedMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { diaryRepository.observeSortedMeals().first() }
        )

    fun orderMeals(meals: List<Meal>) {
        viewModelScope.launch {
            val map = meals.mapIndexed { index, meal -> meal.id to index }.toMap()
            diaryRepository.updateMealsRanks(map)
        }
    }

    fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            diaryRepository.updateMeal(meal)
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            diaryRepository.deleteMeal(meal)
        }
    }

    fun createMeal(name: String, from: LocalTime, to: LocalTime) {
        viewModelScope.launch {
            diaryRepository.createMeal(
                name = name,
                from = from,
                to = to
            )
        }
    }

    fun formatTime(time: LocalTime) = stringFormatRepository.formatTime(time)
}
