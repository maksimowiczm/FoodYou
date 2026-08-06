package com.maksimowiczm.foodyou.features.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LanguageViewModel(private val deviceSettingsRepository: DeviceSettingsRepository) :
    ViewModel() {
    private val translationFlow =
        deviceSettingsRepository
            .observe()
            .map { it.language }
            .map { language -> languages.firstOrNull { it.language == language } }

    val translation =
        translationFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking { translationFlow.first() },
        )

    fun onLanguageSelect(translation: Translation?) {
        viewModelScope.launch {
            deviceSettingsRepository.update { it.copy(language = translation?.language) }
        }
    }
}
