package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.data.DiaryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MealsSettingsScreenViewModel(diaryRepository: DiaryRepository) : ViewModel() {
    val meals = diaryRepository.observeMeals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = emptyList()
    )
}
