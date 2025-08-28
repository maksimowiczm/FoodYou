package com.maksimowiczm.foodyou.business.settings.infrastructure

import com.maksimowiczm.foodyou.business.settings.domain.EnglishUS
import com.maksimowiczm.foodyou.business.settings.domain.SettingsRepository
import com.maksimowiczm.foodyou.business.settings.domain.Translation
import com.maksimowiczm.foodyou.business.settings.domain.TranslationRepository
import com.maksimowiczm.foodyou.business.settings.domain.languages
import com.maksimowiczm.foodyou.business.shared.application.system.SystemDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class TranslationRepositoryImpl(
    private val systemDetails: SystemDetails,
    private val settingsRepository: SettingsRepository,
) : TranslationRepository {
    override fun observe(): Flow<List<Translation>> = flowOf(languages)

    override fun observeCurrent(): Flow<Translation> =
        systemDetails.languageTag.map { currentLanguage ->
            languages.firstOrNull { it.languageTag == currentLanguage } ?: EnglishUS
        }

    override suspend fun setTranslation(translation: Translation?) {
        if (translation == null) {
            systemDetails.setSystemLanguage()
        } else {
            systemDetails.setLanguage(translation.languageTag)
        }

        settingsRepository.update { copy(showTranslationWarning = true) }
    }
}
