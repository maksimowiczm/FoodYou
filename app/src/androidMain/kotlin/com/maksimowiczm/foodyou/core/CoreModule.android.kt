package com.maksimowiczm.foodyou.core

import com.maksimowiczm.foodyou.core.util.DateFormatter
import com.maksimowiczm.foodyou.core.util.SystemDetails
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

actual fun Module.systemDetails() {
    factoryOf(::SystemDetails)
}

actual fun Module.dateFormatter() {
    factoryOf(::DateFormatter)
}
