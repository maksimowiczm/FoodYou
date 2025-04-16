package com.maksimowiczm.foodyou.feature.productredesign

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productModule = module {
    viewModelOf(::CreateProductViewModel)
}
