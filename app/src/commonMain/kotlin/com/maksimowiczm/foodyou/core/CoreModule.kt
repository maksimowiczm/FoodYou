package com.maksimowiczm.foodyou.core

import com.maksimowiczm.foodyou.core.data.DateProvider
import com.maksimowiczm.foodyou.core.data.DateProviderImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun Module.systemInfoRepository()

expect fun Module.stringFormatRepository()

val coreModule = module {
    single { DateProviderImpl() }.bind<DateProvider>()

    systemInfoRepository()
    stringFormatRepository()
}
