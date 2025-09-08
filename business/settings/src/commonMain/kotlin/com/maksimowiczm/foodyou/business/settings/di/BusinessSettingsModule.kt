package com.maksimowiczm.foodyou.business.settings.di

import com.maksimowiczm.foodyou.business.settings.domain.ObserveChangelogUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val businessSettingsModule = module { factoryOf(::ObserveChangelogUseCase) }
