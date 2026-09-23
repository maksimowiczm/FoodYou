package com.maksimowiczm.foodyou.userproduct

import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

internal fun Module.userProduct() {
    factoryOf(::UserProductService)
}
