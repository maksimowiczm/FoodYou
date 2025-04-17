package com.maksimowiczm.foodyou.feature.productredesign

import com.maksimowiczm.foodyou.feature.productredesign.data.ProductRepository
import com.maksimowiczm.foodyou.feature.productredesign.ui.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.productredesign.ui.update.UpdateProductViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productModule = module {
    factoryOf(::ProductRepository)

    viewModelOf(::CreateProductViewModel)
    viewModelOf(::UpdateProductViewModel)
}
