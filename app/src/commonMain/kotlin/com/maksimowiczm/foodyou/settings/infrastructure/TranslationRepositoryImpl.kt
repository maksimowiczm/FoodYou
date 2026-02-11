package com.maksimowiczm.foodyou.settings.infrastructure

import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.common.system.SystemDetails
import com.maksimowiczm.foodyou.settings.domain.entity.Author
import com.maksimowiczm.foodyou.settings.domain.entity.Settings
import com.maksimowiczm.foodyou.settings.domain.entity.Translation
import com.maksimowiczm.foodyou.settings.domain.repository.TranslationRepository
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
private val DarjanZlobec = Author("Darjan Zlobec", "https://www.rtm.si")

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
        //     languageTag = "language-tag",
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
        Translation("Čeština (Česko)", "cs-CZ"),
        Translation("Dansk (Danmark)", "da-DK"),
        Translation("Deutsch (Deutschland)", "de-DE"),
        Translation("Español (España)", "es-ES"),
        Translation("Français (France)", "fr-FR"),
        Translation("Indonesian (Indonesia)", "id-ID"),
        Translation("Italiano (Italia)", "it-IT"),
        Translation("Magyar (Magyarország)", "hu-HU"),
        Translation("Nederlands (Nederland)", "nl-NL", false, grizzleNL),
        Translation("Polski (Polska)", "pl-PL", true, me),
        Translation("Português (Brasil)", "pt-BR"),
        Translation("Slovenščina (Slovenija)", "sl-SI", false, DarjanZlobec),
        Translation("Türkçe (Türkiye)", "tr-TR", false, mikropsoft),
        Translation("Русский (Россия)", "ru-RU"),
        Translation("Українська (Україна)", "uk-UA"),
        Translation("العربية (المملكة العربية السعودية)", "ar-SA"),
        Translation("简体中文", "zh-CN"),
    )
