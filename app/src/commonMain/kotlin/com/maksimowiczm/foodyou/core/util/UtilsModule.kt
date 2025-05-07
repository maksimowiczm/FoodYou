package com.maksimowiczm.foodyou.core.util

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun Module.systemDetails()

expect fun Module.dateFormatter()

val utilsModule = module {
    single { DateProviderImpl() }.bind<DateProvider>()

    systemDetails()
    dateFormatter()
}
