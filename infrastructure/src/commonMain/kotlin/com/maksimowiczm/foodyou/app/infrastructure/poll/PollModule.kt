package com.maksimowiczm.foodyou.app.infrastructure.poll

import com.maksimowiczm.foodyou.business.settings.domain.PollRepository
import com.maksimowiczm.foodyou.business.shared.di.userPreferencesRepositoryOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.pollModule() {
    factoryOf(::StaticPollRepository).bind<PollRepository>()
    userPreferencesRepositoryOf(::DataStorePollPreferencesRepository)
}
