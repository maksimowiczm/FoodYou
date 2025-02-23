package com.maksimowiczm.foodyou.feature.diary.ui.goalssettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.DailyGoals
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

class GoalsSettingsViewModel(private val diaryRepository: DiaryRepository) : ViewModel() {
    val dailyGoals = diaryRepository.observeDailyGoals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2000),
        initialValue = runBlocking { diaryRepository.observeDailyGoals().first() }
    )

    suspend fun onSaveDailyGoals(goals: DailyGoals) {
        diaryRepository.setDailyGoals(goals)
    }
}
