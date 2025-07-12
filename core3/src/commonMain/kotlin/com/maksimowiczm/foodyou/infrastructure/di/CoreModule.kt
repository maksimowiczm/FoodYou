package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.util.DateProvider
import com.maksimowiczm.foodyou.core.util.DateProviderImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun Module.systemDetails()

val coreModule = module {
    single { DateProviderImpl() }.bind<DateProvider>()

    systemDetails()
}
