package com.maksimowiczm.foodyou.preferences.infrastructure

import com.maksimowiczm.foodyou.preferences.domain.LanguagePreference
import kotlinx.coroutines.flow.Flow
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

expect class LanguagePreferenceProvider : UserPreferenceProvider<LanguagePreference> {
    override val key: String

    override fun observe(): Flow<LanguagePreference>

    override suspend fun update(transform: (LanguagePreference) -> LanguagePreference)
}

expect fun Module.languagePreferenceProvider(): KoinDefinition<out LanguagePreferenceProvider>
