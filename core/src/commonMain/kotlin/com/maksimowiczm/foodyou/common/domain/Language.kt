package com.maksimowiczm.foodyou.common.domain

import kotlinx.serialization.Serializable

/**
 * @param displayName Language name in English
 * @param language ISO 639-1 language code
 * @param country ISO 3166-1 alpha-2
 */
@Serializable
enum class Language(val displayName: String, val language: String, val country: String) {
    English("English", "en", "US"),
    Catalan("Catalan", "ca", "ES"),
    Czech("Czech", "cs", "CZ"),
    Danish("Danish", "da", "DK"),
    German("German", "de", "DE"),
    Spanish("Spanish", "es", "ES"),
    French("French", "fr", "FR"),
    Indonesian("Indonesian", "id", "ID"),
    Italian("Italian", "it", "IT"),
    Hungarian("Hungarian", "hu", "HU"),
    Dutch("Dutch", "nl", "NL"),
    Polish("Polish", "pl", "PL"),
    PortugueseBrazil("Portuguese (Brazil)", "pt", "BR"),
    PortuguesePortugal("Portuguese (Portugal)", "pt", "PT"),
    Slovenian("Slovenian", "sl", "SI"),
    Turkish("Turkish", "tr", "TR"),
    Russian("Russian", "ru", "RU"),
    Ukrainian("Ukrainian", "uk", "UA"),
    Arabic("Arabic", "ar", "SA"),
    ChineseSimplified("Chinese (Simplified)", "zh", "CN");

    /** BCP 47 language tag */
    val tag = "$language-$country"

    companion object {
        private val tagMap: Map<String, Language> by lazy {
            entries.associateBy { it.tag.lowercase() }
        }

        private val languageMap: Map<String, Language> by lazy {
            val map = mutableMapOf<String, Language>()
            entries.forEach { language ->
                val key = language.language.lowercase()
                if (!map.containsKey(key)) {
                    map[key] = language
                }
            }
            map
        }

        fun fromTag(tag: String): Language? {
            val normalizedTag = tag.replace("_", "-").lowercase()

            // Try exact match
            tagMap[normalizedTag]?.let {
                return it
            }

            // Try language only match
            val langPart = normalizedTag.substringBefore("-")
            return languageMap[langPart]
        }
    }
}
