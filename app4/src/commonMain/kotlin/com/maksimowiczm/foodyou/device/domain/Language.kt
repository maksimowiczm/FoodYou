package com.maksimowiczm.foodyou.device.domain

sealed interface Language {
    data object System : Language

    /** @param tag ISO 3166-1 alpha-2 country code. */
    data class Tag(val tag: String) : Language
}
