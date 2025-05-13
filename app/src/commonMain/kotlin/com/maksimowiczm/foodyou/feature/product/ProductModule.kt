package com.maksimowiczm.foodyou.feature.product

import com.maksimowiczm.foodyou.feature.product.domain.ProductRepository
import com.maksimowiczm.foodyou.feature.product.ui.create.CreateProductScreenViewModel
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenViewModel
import com.maksimowiczm.foodyou.feature.product.ui.update.UpdateProductScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productModule = module {
    factoryOf(::ProductRepository)

    viewModelOf(::CreateProductScreenViewModel)
    viewModelOf(::UpdateProductScreenViewModel)
    viewModelOf(::DownloadProductScreenViewModel)
}
