package com.maksimowiczm.foodyou.feature.settings.language.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.business.settings.domain.Translation
import com.maksimowiczm.foodyou.business.settings.domain.TranslationRepository
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class LanguageViewModel(
    private val translationRepository: TranslationRepository,
    private val settingsRepository: UserPreferencesRepository<Settings>,
) : ViewModel() {

    private val translationsFlow = translationRepository.observe()

    val translations =
        translationsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = runBlocking { translationsFlow.first() },
        )

    private val currentTranslationFlow = translationRepository.observeCurrent()

    val translation =
        currentTranslationFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = runBlocking { currentTranslationFlow.first() },
        )

    val showTranslationWarning =
        settingsRepository
            .observe()
            .map { it.showTranslationWarning }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

    fun selectTranslation(translation: Translation?) {
        viewModelScope.launch { translationRepository.setTranslation(translation) }
    }

    fun hideTranslationWarning() {
        viewModelScope.launch { settingsRepository.update { copy(showTranslationWarning = false) } }
    }
}
