package com.maksimowiczm.foodyou.app.business.shared.domain.changelog

import kotlinx.datetime.LocalDate

interface Changelog {
    val currentVersion: Version?
    val versions: List<Version>
}

data class Version(
    val version: String,
    val date: LocalDate,
    val newFeatures: List<String> = emptyList(),
    val changes: List<String> = emptyList(),
    val bugFixes: List<String> = emptyList(),
    val translations: List<String> = emptyList(),
    val notes: String? = null,
    val isPreview: Boolean = false,
)
