package com.maksimowiczm.foodyou.core.feature.product

import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepository
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepositoryImpl
import com.maksimowiczm.foodyou.core.feature.product.ui.crud.create.CreateProductViewModel
import com.maksimowiczm.foodyou.core.feature.product.ui.crud.update.UpdateProductViewModel
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val productsModule = module {
    factoryOf(::ProductRepositoryImpl).bind<ProductRepository>()

    viewModelOf(::CreateProductViewModel)
    viewModelOf(::UpdateProductViewModel)
}

/**
 * ProductFeature is a feature that provides functionality for managing products.
 */
object ProductFeature : Feature.Koin {
    override fun KoinApplication.setup() {
        modules(productsModule)
    }
}
