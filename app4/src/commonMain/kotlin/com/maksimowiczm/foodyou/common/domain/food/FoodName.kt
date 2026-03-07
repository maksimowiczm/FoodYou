package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.Language

data class FoodName(
    val english: String? = null,
    val catalan: String? = null,
    val czech: String? = null,
    val danish: String? = null,
    val german: String? = null,
    val spanish: String? = null,
    val french: String? = null,
    val indonesian: String? = null,
    val italian: String? = null,
    val hungarian: String? = null,
    val dutch: String? = null,
    val polish: String? = null,
    val portugueseBrazil: String? = null,
    val slovenian: String? = null,
    val turkish: String? = null,
    val russian: String? = null,
    val ukrainian: String? = null,
    val arabic: String? = null,
    val chineseSimplified: String? = null,
    val fallback: String,
) {
    private val list: List<String?> =
        listOf(
            english,
            catalan,
            czech,
            danish,
            german,
            spanish,
            french,
            indonesian,
            italian,
            hungarian,
            dutch,
            polish,
            portugueseBrazil,
            slovenian,
            turkish,
            russian,
            ukrainian,
            arabic,
            chineseSimplified,
            fallback,
        )

    init {
        list.forEach {
            if (it != null) {
                require(it.isNotBlank()) { "Food name cannot be blank if provided" }
            }
        }
    }

    operator fun get(language: Language): String? =
        when (language) {
            Language.English -> english
            Language.Catalan -> catalan
            Language.Czech -> czech
            Language.Danish -> danish
            Language.German -> german
            Language.Spanish -> spanish
            Language.French -> french
            Language.Indonesian -> indonesian
            Language.Italian -> italian
            Language.Hungarian -> hungarian
            Language.Dutch -> dutch
            Language.Polish -> polish
            Language.PortugueseBrazil -> portugueseBrazil
            Language.Slovenian -> slovenian
            Language.Turkish -> turkish
            Language.Russian -> russian
            Language.Ukrainian -> ukrainian
            Language.Arabic -> arabic
            Language.ChineseSimplified -> chineseSimplified
        }

    fun contains(text: String): Boolean {
        return list.any { it?.contains(text, ignoreCase = true) == true }
    }

    companion object {
        fun requireAll(
            english: String?,
            catalan: String?,
            czech: String?,
            danish: String?,
            german: String?,
            spanish: String?,
            french: String?,
            indonesian: String?,
            italian: String?,
            hungarian: String?,
            dutch: String?,
            polish: String?,
            portugueseBrazil: String?,
            slovenian: String?,
            turkish: String?,
            russian: String?,
            ukrainian: String?,
            arabic: String?,
            chineseSimplified: String?,
            fallback: String,
        ) =
            FoodName(
                english = english,
                catalan = catalan,
                czech = czech,
                danish = danish,
                german = german,
                spanish = spanish,
                french = french,
                indonesian = indonesian,
                italian = italian,
                hungarian = hungarian,
                dutch = dutch,
                polish = polish,
                portugueseBrazil = portugueseBrazil,
                slovenian = slovenian,
                turkish = turkish,
                russian = russian,
                ukrainian = ukrainian,
                arabic = arabic,
                chineseSimplified = chineseSimplified,
                fallback = fallback,
            )
    }
}
