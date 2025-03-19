package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.data.DiaryRepository
import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.data.preferences.DiaryPreferences
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
    private val diaryRepository: DiaryRepository,
    private val stringFormatRepository: StringFormatRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private fun DiaryRepository.observeSortedMeals() =
        observeMeals().map { it.sortedBy { it.rank } }

    val sortedMeals = diaryRepository.observeSortedMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { diaryRepository.observeSortedMeals().first() }
        )

    val useTimeBasedSorting = dataStore
        .observe(DiaryPreferences.timeBasedSorting)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { dataStore.get(DiaryPreferences.timeBasedSorting) ?: false }
        )

    val allDayMealsAsCurrentlyHappening = dataStore
        .observe(DiaryPreferences.allDayMealsAsCurrentlyHappening)
        .map { it ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking {
                dataStore.get(DiaryPreferences.allDayMealsAsCurrentlyHappening) ?: false
            }
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

    fun toggleTimeBasedSorting(state: Boolean) {
        viewModelScope.launch {
            dataStore.set(DiaryPreferences.timeBasedSorting to state)
        }
    }

    fun toggleAllDayMealsAsCurrentlyHappening(state: Boolean) {
        viewModelScope.launch {
            dataStore.set(DiaryPreferences.allDayMealsAsCurrentlyHappening to state)
        }
    }

    fun formatTime(time: LocalTime) = stringFormatRepository.formatTime(time)
}
