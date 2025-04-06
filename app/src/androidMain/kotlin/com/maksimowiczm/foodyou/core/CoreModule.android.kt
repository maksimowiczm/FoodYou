package com.maksimowiczm.foodyou.core

import com.maksimowiczm.foodyou.core.data.StringFormatRepository
import com.maksimowiczm.foodyou.core.data.SystemInfoRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

actual fun Module.systemInfoRepository() {
    factoryOf(::SystemInfoRepository)
}

actual fun Module.stringFormatRepository() {
    factoryOf(::StringFormatRepository)
}
