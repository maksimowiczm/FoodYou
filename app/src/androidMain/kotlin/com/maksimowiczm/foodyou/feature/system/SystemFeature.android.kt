package com.maksimowiczm.foodyou.feature.system

import com.maksimowiczm.foodyou.feature.system.data.StringFormatRepository
import com.maksimowiczm.foodyou.feature.system.data.SystemInfoRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

actual fun Module.stringFormatRepository() {
    factoryOf(::StringFormatRepository)
}

actual fun Module.systemInfoRepository() {
    factoryOf(::SystemInfoRepository)
}
