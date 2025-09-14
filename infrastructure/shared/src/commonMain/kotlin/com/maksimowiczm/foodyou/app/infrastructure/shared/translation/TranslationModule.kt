package com.maksimowiczm.foodyou.app.infrastructure.shared.translation

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.app.business.shared.domain.translation.TranslationRepository
import org.koin.core.module.Module
import org.koin.dsl.bind

fun Module.translationModule() {
    factory {
            TranslationRepositoryImpl(
                systemDetails = get(),
                settingsRepository = userPreferencesRepository(),
            )
        }
        .bind<TranslationRepository>()
}
