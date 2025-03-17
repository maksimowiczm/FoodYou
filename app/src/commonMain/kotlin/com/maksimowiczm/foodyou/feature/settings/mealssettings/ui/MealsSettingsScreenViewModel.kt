package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.data.DiaryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

class MealsSettingsScreenViewModel(diaryRepository: DiaryRepository) : ViewModel() {
    private fun DiaryRepository.observeSortedMeals() =
        observeMeals().map { it.sortedBy { it.lexoRank } }

    val meals = diaryRepository.observeSortedMeals()
        .map { it.sortedBy { it.lexoRank } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { diaryRepository.observeSortedMeals().first() }
        )
}
