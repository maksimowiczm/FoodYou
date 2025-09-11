package com.maksimowiczm.foodyou.app.business.opensource.domain.changelog

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

fun Module.changelogModule() {
    factoryOf(::ObserveChangelogUseCase)
}
