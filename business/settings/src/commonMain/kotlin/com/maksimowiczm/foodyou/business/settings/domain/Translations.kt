package com.maksimowiczm.foodyou.business.settings.domain

data class Translation(
    val languageName: String,
    val languageTag: String,
    val authorsStrings: List<Author>,
    val isVerified: Boolean = false,
) {
    internal constructor(
        languageName: String,
        languageTag: String,
        isVerified: Boolean = false,
        vararg authors: Author,
    ) : this(
        languageName = languageName,
        languageTag = languageTag,
        authorsStrings = authors.toList(),
        isVerified = isVerified,
    )
}

data class Author(val name: String, val link: String? = null)

// Me
private val me = Author("Mateusz Maksimowicz", "https://github.com/maksimowiczm")

// Someone who helped with the translation
private val grizzleNL = Author("GrizzleNL", "https://grizzle.nl")
private val mikropsoft = Author("mikropsoft", "https://github.com/mikropsoft")

internal val EnglishUS =
    Translation(
        languageName = "English (United States)",
        languageTag = "en-US",
        authorsStrings = listOf(me),
        isVerified = true,
    )

internal val languages =
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
