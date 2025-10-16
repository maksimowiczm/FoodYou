package com.maksimowiczm.foodyou.common.domain

/**
 * @param displayName Language name in English
 * @param language ISO 639-1 language code
 * @param country ISO 3166-1 alpha-2
 */
enum class Language(val displayName: String, val language: String, val country: String) {
    English("English", "en", "US"),
    Catalan("Catalan", "ca", "ES"),
    Danish("Danish", "da", "DK"),
    German("German", "de", "DE"),
    Spanish("Spanish", "es", "ES"),
    French("French", "fr", "FR"),
    Italian("Italian", "it", "IT"),
    Hungarian("Hungarian", "hu", "HU"),
    Dutch("Dutch", "nl", "NL"),
    Polish("Polish", "pl", "PL"),
    PortugueseBrazil("Portuguese (Brazil)", "pt", "BR"),
    Turkish("Turkish", "tr", "TR"),
    Russian("Russian", "ru", "RU"),
    Ukrainian("Ukrainian", "uk", "UA"),
    Arabic("Arabic", "ar", "SA"),
    ChineseSimplified("Chinese (Simplified)", "zh", "CN");

    /** BCP 47 language tag */
    val tag = "$language-$country"
}
