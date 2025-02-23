package com.maksimowiczm.foodyou.feature.product

import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.product.data.ProductRepository
import com.maksimowiczm.foodyou.feature.product.ui.crud.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.product.ui.crud.update.UpdateProductViewModel
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * ProductFeature is a feature that provides functionality for managing products.
 */
abstract class ProductFeature(
    productRepository: Module.() -> KoinDefinition<ProductRepository>,
    val settingsRoute: Any?
) : Feature.Koin {
    private val module = module {
        viewModelOf(::CreateProductViewModel)
        viewModelOf(::UpdateProductViewModel)

        productRepository().bind()
    }

    final override fun KoinApplication.setup() {
        modules(module)

        configure()
    }

    abstract fun KoinApplication.configure()
}
