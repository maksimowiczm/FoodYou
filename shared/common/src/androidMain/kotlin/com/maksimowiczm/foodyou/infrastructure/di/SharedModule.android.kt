package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.shared.common.infrastructure.system.SystemDetailsImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

actual fun Module.systemDetails() {
    factoryOf(::SystemDetailsImpl)
}
