package com.maksimowiczm.foodyou.feature.product

import com.maksimowiczm.foodyou.feature.product.data.ProductRepository
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.product.ui.update.UpdateProductViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productModule = module {
    factoryOf(::ProductRepository)

    viewModelOf(::CreateProductViewModel)
    viewModelOf(::UpdateProductViewModel)
}
