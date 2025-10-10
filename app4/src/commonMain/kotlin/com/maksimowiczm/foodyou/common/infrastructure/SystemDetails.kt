package com.maksimowiczm.foodyou.common.infrastructure

import kotlinx.coroutines.flow.Flow
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

expect class SystemDetails {
    /**
     * This value is derived from the system configuration and represents the ISO 3166-1 alpha-2
     * country code.
     */
    val languageTag: Flow<String>

    fun setLanguage(tag: String)

    fun setSystemLanguage()
}

expect fun Module.systemDetails(): KoinDefinition<out SystemDetails>
