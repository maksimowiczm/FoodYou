package com.maksimowiczm.foodyou.common.infrastructure.system

import com.maksimowiczm.foodyou.common.system.SystemDetails
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

actual fun Module.systemDetailsDefinition() {
    singleOf(::AndroidSystemDetails).bind<SystemDetails>()
}
