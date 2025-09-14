package com.maksimowiczm.foodyou.app.infrastructure.shared.translation

import com.maksimowiczm.foodyou.app.business.shared.domain.settings.Settings
import com.maksimowiczm.foodyou.app.business.shared.domain.translation.Author
import com.maksimowiczm.foodyou.app.business.shared.domain.translation.Translation
import com.maksimowiczm.foodyou.app.business.shared.domain.translation.TranslationRepository
import com.maksimowiczm.foodyou.app.infrastructure.shared.SystemDetails
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class TranslationRepositoryImpl(
    private val systemDetails: SystemDetails,
    private val settingsRepository: UserPreferencesRepository<Settings>,
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

// Me
private val me = Author("Mateusz Maksimowicz", "https://github.com/maksimowiczm")

// Someone who helped with the translation
private val grizzleNL = Author("GrizzleNL", "https://grizzle.nl")
private val mikropsoft = Author("mikropsoft", "https://github.com/mikropsoft")

private val EnglishUS =
    Translation(
        languageName = "English (United States)",
        languageTag = "en-US",
        authorsStrings = listOf(me),
        isVerified = true,
    )

private val languages =
    listOf(
        // If you'd like to be credited for your translations, please add your name here.
        // "language name (Country)" to Translation(
        //     tag = "language-tag",
        //      listOf(
        //         Author(
        //             name = "Your Name",
        //             // Optional link to your website or profile
        //             link = "https://example.com"
        //         )
        //     )
        // ),
        EnglishUS,
        Translation("Català (Espanya)", "ca-ES"),
        Translation("Dansk (Danmark)", "da-DK"),
        Translation("Deutsch (Deutschland)", "de-DE"),
        Translation("Español (España)", "es-ES"),
        Translation("Français (France)", "fr-FR"),
        Translation("Italiano (Italia)", "it-IT"),
        Translation("Magyar (Magyarország)", "hu-HU"),
        Translation("Nederlands (Nederland)", "nl-NL", false, grizzleNL),
        Translation("Polski (Polska)", "pl-PL", true, me),
        Translation("Português (Brasil)", "pt-BR"),
        Translation("Türkçe (Türkiye)", "tr-TR", false, mikropsoft),
        Translation("Русский (Россия)", "ru-RU"),
        Translation("Українська (Україна)", "uk-UA"),
        Translation("العربية (المملكة العربية السعودية)", "ar-SA"),
        Translation("简体中文", "zh-CN"),
    )
