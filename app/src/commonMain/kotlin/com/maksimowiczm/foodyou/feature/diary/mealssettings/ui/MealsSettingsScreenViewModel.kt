package com.maksimowiczm.foodyou.feature.diary.mealssettings.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.data.StringFormatRepository
import com.maksimowiczm.foodyou.core.ext.get
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.feature.diary.core.data.DiaryPreferences
import com.maksimowiczm.foodyou.feature.diary.core.data.meal.MealRepository
import com.maksimowiczm.foodyou.feature.diary.mealssettings.domain.Meal
import com.maksimowiczm.foodyou.feature.diary.mealssettings.domain.ObserveMealsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalTime

internal class MealsSettingsScreenViewModel(
    observeMealsUseCase: ObserveMealsUseCase,
    private val mealRepository: MealRepository,
    private val stringFormatRepository: StringFormatRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    val meals = observeMealsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
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
