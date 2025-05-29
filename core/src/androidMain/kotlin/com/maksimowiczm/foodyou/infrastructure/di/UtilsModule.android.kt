package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.util.SystemDetails
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

actual fun Module.systemDetails() {
    factoryOf(::SystemDetails)
}
