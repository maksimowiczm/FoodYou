package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.settings.domain.SettingsRepository
import com.maksimowiczm.foodyou.business.settings.domain.TranslationRepository
import com.maksimowiczm.foodyou.business.settings.infrastructure.TranslationRepositoryImpl
import com.maksimowiczm.foodyou.business.settings.infrastructure.datastore.DataStoreSettingsRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val businessSettingsModule = module {
    factoryOf(::TranslationRepositoryImpl).bind<TranslationRepository>()
    factoryOf(::DataStoreSettingsRepository).bind<SettingsRepository>()
}
