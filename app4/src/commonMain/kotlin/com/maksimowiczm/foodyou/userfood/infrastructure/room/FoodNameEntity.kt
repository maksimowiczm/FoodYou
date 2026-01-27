package com.maksimowiczm.foodyou.userfood.infrastructure.room

import androidx.room.ColumnInfo

internal data class FoodNameEntity(
    @ColumnInfo(name = "en") val english: String? = null,
    @ColumnInfo(name = "ca") val catalan: String? = null,
    @ColumnInfo(name = "da") val danish: String? = null,
    @ColumnInfo(name = "de") val german: String? = null,
    @ColumnInfo(name = "es") val spanish: String? = null,
    @ColumnInfo(name = "fr") val french: String? = null,
    @ColumnInfo(name = "id") val indonesian: String? = null,
    @ColumnInfo(name = "it") val italian: String? = null,
    @ColumnInfo(name = "hu") val hungarian: String? = null,
    @ColumnInfo(name = "nl") val dutch: String? = null,
    @ColumnInfo(name = "pl") val polish: String? = null,
    @ColumnInfo(name = "pt-BR") val portugueseBrazil: String? = null,
    @ColumnInfo(name = "sl") val slovenian: String? = null,
    @ColumnInfo(name = "tr") val turkish: String? = null,
    @ColumnInfo(name = "ru") val russian: String? = null,
    @ColumnInfo(name = "uk") val ukrainian: String? = null,
    @ColumnInfo(name = "ar") val arabic: String? = null,
    @ColumnInfo(name = "zh-CN") val chineseSimplified: String? = null,
) {
    init {
        listOf(
                english,
                catalan,
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
            )
            .any { !it.isNullOrBlank() }
            .also { require(it) { "At least one language must be provided" } }
    }

    val fallback: String
        get() =
            listOfNotNull(
                    english,
                    catalan,
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
                )
                .first()
}
