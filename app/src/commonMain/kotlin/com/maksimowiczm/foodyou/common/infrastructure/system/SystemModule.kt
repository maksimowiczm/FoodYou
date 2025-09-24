package com.maksimowiczm.foodyou.common.infrastructure.system

import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

expect fun Module.systemDetailsDefinition()

fun Module.systemModule() {
    systemDetailsDefinition()
    factoryOf(::DateProviderImpl).bind<DateProvider>()
}
