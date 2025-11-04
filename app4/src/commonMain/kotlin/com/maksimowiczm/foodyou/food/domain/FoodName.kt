package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.Language

data class FoodName(
    val english: String? = null,
    val catalan: String? = null,
    val danish: String? = null,
    val german: String? = null,
    val spanish: String? = null,
    val french: String? = null,
    val italian: String? = null,
    val hungarian: String? = null,
    val dutch: String? = null,
    val polish: String? = null,
    val portugueseBrazil: String? = null,
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
            danish,
            german,
            spanish,
            french,
            italian,
            hungarian,
            dutch,
            polish,
            portugueseBrazil,
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
            Language.Danish -> danish
            Language.German -> german
            Language.Spanish -> spanish
            Language.French -> french
            Language.Italian -> italian
            Language.Hungarian -> hungarian
            Language.Dutch -> dutch
            Language.Polish -> polish
            Language.PortugueseBrazil -> portugueseBrazil
            Language.Turkish -> turkish
            Language.Russian -> russian
            Language.Ukrainian -> ukrainian
            Language.Arabic -> arabic
            Language.ChineseSimplified -> chineseSimplified
        }

    fun contains(text: String): Boolean {
        return list.any { it?.contains(text, ignoreCase = true) == true }
    }
}
