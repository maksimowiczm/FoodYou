package com.maksimowiczm.foodyou.feature.diary.ui.mealssettings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.data.preferences.DiaryPreferences
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import com.maksimowiczm.foodyou.infrastructure.datastore.get
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalTime

class MealsSettingsScreenViewModel(
    private val mealRepository: MealRepository,
    private val stringFormatRepository: StringFormatRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private fun MealRepository.observeSortedMeals() = observeMeals().map { it.sortedBy { it.rank } }

    val sortedMeals = mealRepository.observeSortedMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { mealRepository.observeSortedMeals().first() }
        )

    val useTimeBasedSorting = dataStore
        .observe(DiaryPreferences.timeBasedSorting)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { dataStore.get(DiaryPreferences.timeBasedSorting) ?: false }
        )

    val includeAllDayMeals = dataStore
        .observe(DiaryPreferences.includeAllDayMeals)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking {
                dataStore.get(DiaryPreferences.includeAllDayMeals) ?: false
            }
        )

    fun updateMealsRanks(meals: List<Meal>) {
        viewModelScope.launch {
            val map = meals.mapIndexed { index, meal -> meal.id to index }.toMap()
            mealRepository.updateMealsRanks(map)
        }
    }

    fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.updateMeal(meal)
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.deleteMeal(meal)
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

    fun toggleTimeBasedSorting(state: Boolean) {
        viewModelScope.launch {
            dataStore.set(DiaryPreferences.timeBasedSorting to state)
        }
    }

    fun toggleIncludeAllDayMeals(state: Boolean) {
        viewModelScope.launch {
            dataStore.set(DiaryPreferences.includeAllDayMeals to state)
        }
    }

    fun formatTime(time: LocalTime) = stringFormatRepository.formatTime(time)
}
