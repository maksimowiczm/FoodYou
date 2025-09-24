package com.maksimowiczm.foodyou.poll.infrastructure

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepositoryOf
import com.maksimowiczm.foodyou.poll.domain.repository.PollRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.pollInfrastructureModule() {
    factoryOf(::StaticPollRepository).bind<PollRepository>()
    userPreferencesRepositoryOf(::DataStorePollPreferencesRepository)
}
