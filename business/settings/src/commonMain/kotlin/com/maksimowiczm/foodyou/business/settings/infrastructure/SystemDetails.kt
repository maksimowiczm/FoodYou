package com.maksimowiczm.foodyou.business.settings.infrastructure

import kotlinx.coroutines.flow.Flow

expect class SystemDetails {

    /**
     * This value is derived from the system configuration and represents the ISO 3166-1 alpha-2
     * country code.
     */
    val languageTag: Flow<String>

    fun setLanguage(tag: String)

    fun setSystemLanguage()
}
