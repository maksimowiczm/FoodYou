package com.maksimowiczm.foodyou.app.infrastructure.poll

import com.maksimowiczm.foodyou.app.business.opensource.di.userPreferencesRepositoryOf
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.PollRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.pollModule() {
    factoryOf(::StaticPollRepository).bind<PollRepository>()
    userPreferencesRepositoryOf(::DataStorePollPreferencesRepository)
}
