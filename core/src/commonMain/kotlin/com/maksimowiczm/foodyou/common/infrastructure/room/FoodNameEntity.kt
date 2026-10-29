package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room3.*

data class FoodNameEntity(
    @ColumnInfo(name = "en") val english: String? = null,
    @ColumnInfo(name = "cs") val czech: String? = null,
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
    @ColumnInfo(name = "pt-PT") val portuguesePortugal: String? = null,
    @ColumnInfo(name = "sl") val slovenian: String? = null,
    @ColumnInfo(name = "tr") val turkish: String? = null,
    @ColumnInfo(name = "ru") val russian: String? = null,
    @ColumnInfo(name = "uk") val ukrainian: String? = null,
    @ColumnInfo(name = "ar") val arabic: String? = null,
    @ColumnInfo(name = "zh-CN") val chineseSimplified: String? = null,
    @ColumnInfo(name = "fallback") val fallback: String,
)

/**
 * SQL query snippet for selecting a localized food name based on `:languageCode`. Note: The query
 * embedding this snippet MUST alias the target table/view as `s`.
 */
const val SIMPLE_NAME_SELECT =
    """
COALESCE(
    CASE :languageCode
        WHEN 'en-US' THEN s.name_en
        WHEN 'ca-ES' THEN s.name_ca
        WHEN 'cs-CZ' THEN s.name_cs
        WHEN 'da-DK' THEN s.name_da
        WHEN 'de-DE' THEN s.name_de
        WHEN 'es-ES' THEN s.name_es
        WHEN 'fr-FR' THEN s.name_fr
        WHEN 'id-ID' THEN s.name_id
        WHEN 'it-IT' THEN s.name_it
        WHEN 'hu-HU' THEN s.name_hu
        WHEN 'nl-NL' THEN s.name_nl
        WHEN 'pl-PL' THEN s.name_pl
        WHEN 'sl-SI' THEN s.name_sl
        WHEN 'pt-BR' THEN s.`name_pt-BR`
        WHEN 'pt-PT' THEN s.`name_pt-PT`
        WHEN 'tr-TR' THEN s.name_tr
        WHEN 'ru-RU' THEN s.name_ru
        WHEN 'uk-UA' THEN s.name_uk
        WHEN 'ar-SA' THEN s.name_ar
        WHEN 'zh-CN' THEN s.`name_zh-CN`
        ELSE s.name_fallback
    END,
    s.name_fallback,
    s.name_en,
    s.name_ca,
    s.name_cs,
    s.name_da,
    s.name_de,
    s.name_es,
    s.name_fr,
    s.name_it,
    s.name_id,
    s.name_hu,
    s.name_nl,
    s.name_pl,
    s.name_sl,
    s.`name_pt-BR`,
    s.`name_pt-PT`,
    s.name_tr,
    s.name_ru,
    s.name_uk,
    s.name_ar,
    s.`name_zh-CN`
)
"""
