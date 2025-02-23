package com.maksimowiczm.foodyou.feature.diary.ui.mealssettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalTime

class MealsSettingsViewModel(
    private val diaryRepository: DiaryRepository,
    private val stringFormatRepository: StringFormatRepository
) : ViewModel() {
    val meals = diaryRepository.observeMeals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = runBlocking {
            diaryRepository.observeMeals().first()
        }
    )

    fun formatTime(time: LocalTime) = stringFormatRepository.formatTime(time)

    suspend fun createMeal(name: String, from: LocalTime, to: LocalTime) {
        viewModelScope.async {
            diaryRepository.createMeal(name, from, to)
        }.await()
    }

    suspend fun updateMeal(meal: Meal) {
        viewModelScope.async {
            diaryRepository.updateMeal(meal)
        }.await()
    }

    suspend fun deleteMeal(meal: Meal) {
        viewModelScope.async {
            diaryRepository.deleteMeal(meal)
        }.await()
    }
}
