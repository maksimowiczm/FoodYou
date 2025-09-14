package com.maksimowiczm.foodyou.app.infrastructure.opensource.poll

import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.PollRepository
import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepositoryOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.pollModule() {
    factoryOf(::StaticPollRepository).bind<PollRepository>()
    userPreferencesRepositoryOf(::DataStorePollPreferencesRepository)
}
