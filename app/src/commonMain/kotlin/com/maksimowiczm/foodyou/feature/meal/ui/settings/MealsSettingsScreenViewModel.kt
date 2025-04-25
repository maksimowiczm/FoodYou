package com.maksimowiczm.foodyou.feature.meal.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.repository.MealRepository
import com.maksimowiczm.foodyou.core.ext.get
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.core.util.DateFormatter
import com.maksimowiczm.foodyou.feature.meal.data.MealPreferences
import com.maksimowiczm.foodyou.feature.meal.domain.Meal
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalTime

internal class MealsSettingsScreenViewModel(
    observeMealsUseCase: ObserveMealsUseCase,
    private val mealRepository: MealRepository,
    private val dateFormatter: DateFormatter,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    val meals = observeMealsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    val useTimeBasedSorting = dataStore
        .observe(MealPreferences.timeBasedSorting)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { dataStore.get(MealPreferences.timeBasedSorting) ?: false }
        )

    val includeAllDayMeals = dataStore
        .observe(MealPreferences.includeAllDayMeals)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking {
                dataStore.get(MealPreferences.includeAllDayMeals) ?: false
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

    fun toggleTimeBasedSorting(state: Boolean) {
        viewModelScope.launch {
            dataStore.set(MealPreferences.timeBasedSorting to state)
        }
    }

    fun toggleIncludeAllDayMeals(state: Boolean) {
        viewModelScope.launch {
            dataStore.set(MealPreferences.includeAllDayMeals to state)
        }
    }

    fun formatTime(time: LocalTime) = dateFormatter.formatTime(time)
}
