package com.maksimowiczm.foodyou.feature.productredesign

import com.maksimowiczm.foodyou.feature.productredesign.ui.CreateProductViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productModule = module {
    viewModelOf(::CreateProductViewModel)
}
