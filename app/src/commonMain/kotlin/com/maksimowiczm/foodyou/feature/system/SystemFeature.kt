package com.maksimowiczm.foodyou.feature.system

import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.system.data.DateProvider
import com.maksimowiczm.foodyou.feature.system.data.DateProviderImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun Module.systemInfoRepository()

expect fun Module.stringFormatRepository()

object SystemFeature : Feature {
    override val module: Module = module {
        single { DateProviderImpl() }.bind<DateProvider>()

        systemInfoRepository()
        stringFormatRepository()
    }
}
