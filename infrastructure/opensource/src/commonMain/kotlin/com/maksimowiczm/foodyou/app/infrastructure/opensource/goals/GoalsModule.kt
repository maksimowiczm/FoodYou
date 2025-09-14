package com.maksimowiczm.foodyou.app.infrastructure.opensource.goals

import com.maksimowiczm.foodyou.goals.domain.repository.GoalsRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.goalsModule() {
    factoryOf(::DataStoreGoalsRepository).bind<GoalsRepository>()
}
