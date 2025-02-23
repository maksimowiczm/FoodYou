package com.maksimowiczm.foodyou.feature.system

import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.system.data.AndroidStringFormatRepository
import com.maksimowiczm.foodyou.feature.system.data.AndroidSystemInfoRepository
import com.maksimowiczm.foodyou.feature.system.data.DateProvider
import com.maksimowiczm.foodyou.feature.system.data.DateProviderImpl
import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import com.maksimowiczm.foodyou.feature.system.data.SystemInfoRepository
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val systemModule = module {
    factoryOf(::AndroidSystemInfoRepository).bind<SystemInfoRepository>()
    factoryOf(::AndroidStringFormatRepository).bind<StringFormatRepository>()

    single { DateProviderImpl() }.bind<DateProvider>()
}

/**
 * SystemFeature is a feature that provides system information.
 */
object SystemFeature : Feature.Koin {
    override fun KoinApplication.setup() {
        modules(systemModule)
    }
}
