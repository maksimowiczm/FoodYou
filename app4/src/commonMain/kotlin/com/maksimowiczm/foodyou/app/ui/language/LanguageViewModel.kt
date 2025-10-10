package com.maksimowiczm.foodyou.app.ui.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import com.maksimowiczm.foodyou.device.domain.Language
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LanguageViewModel(private val deviceRepository: DeviceRepository) : ViewModel() {
    private val translationFlow =
        deviceRepository
            .observe()
            .map { it.language }
            .map { language ->
                when (language) {
                    Language.System -> null
                    is Language.Tag -> languages.firstOrNull { it.languageTag == language.tag }
                }
            }

    val translation =
        translationFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking { translationFlow.first() },
        )

    fun onLanguageSelect(translation: Translation?) {
        val language =
            when (translation) {
                null -> Language.System
                else -> Language.Tag(translation.languageTag)
            }

        viewModelScope.launch {
            val device = deviceRepository.load()
            device.updateLanguageTag(language)
            deviceRepository.save(device)
        }
    }
}
