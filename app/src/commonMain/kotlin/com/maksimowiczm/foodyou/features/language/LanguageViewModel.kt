package com.maksimowiczm.foodyou.features.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.preferences.domain.LanguagePreference
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.domain.observe
import com.maksimowiczm.foodyou.preferences.domain.update
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LanguageViewModel(private val preferencesRepository: UserPreferencesRepository) :
    ViewModel() {
    private val translationFlow =
        preferencesRepository
            .observe<LanguagePreference>()
            .map { it.language }
            .map { language -> languages.firstOrNull { it.language == language } }

    val translation =
        translationFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = runBlocking { translationFlow.first() },
        )

    fun onLanguageSelect(translation: Translation?) {
        viewModelScope.launch {
            preferencesRepository.update<LanguagePreference> {
                if (translation == null) LanguagePreference(null)
                else LanguagePreference(translation.language)
            }
        }
    }
}
