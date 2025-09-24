package com.maksimowiczm.foodyou.settings.infrastructure

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepository
import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepositoryOf
import com.maksimowiczm.foodyou.settings.domain.repository.TranslationRepository
import org.koin.core.module.Module
import org.koin.dsl.bind

internal fun Module.settingsInfrastructureModule() {
    userPreferencesRepositoryOf(::DataStoreSettingsRepository)
    factory {
            TranslationRepositoryImpl(
                systemDetails = get(),
                settingsRepository = userPreferencesRepository(),
            )
        }
        .bind<TranslationRepository>()
}
