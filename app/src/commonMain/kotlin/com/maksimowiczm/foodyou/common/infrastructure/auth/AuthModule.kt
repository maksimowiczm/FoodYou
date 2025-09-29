package com.maksimowiczm.foodyou.common.infrastructure.auth

import com.maksimowiczm.foodyou.common.auth.SessionRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

fun Module.authModule() {
    factoryOf(::SafeSessionRepository).bind<SessionRepository>()
}
