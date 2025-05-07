package com.maksimowiczm.foodyou.core.util

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

actual fun Module.systemDetails() {
    factoryOf(::SystemDetails)
}
