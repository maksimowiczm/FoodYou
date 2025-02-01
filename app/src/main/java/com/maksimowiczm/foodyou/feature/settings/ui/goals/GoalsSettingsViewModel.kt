package com.maksimowiczm.foodyou.feature.settings.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.feature.settings.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

class GoalsSettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val dailyGoals = settingsRepository.observeDailyGoals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2000),
        initialValue = runBlocking { settingsRepository.observeDailyGoals().first() }
    )

    suspend fun onSaveDailyGoals(goals: DailyGoals) {
        settingsRepository.setDailyGoals(goals)
    }
}
