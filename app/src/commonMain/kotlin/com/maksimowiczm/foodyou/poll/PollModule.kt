package com.maksimowiczm.foodyou.poll

import com.maksimowiczm.foodyou.poll.domain.pollDomainModule
import com.maksimowiczm.foodyou.poll.infrastructure.pollInfrastructureModule
import org.koin.dsl.module

val pollModule = module {
    pollDomainModule()
    pollInfrastructureModule()
}
