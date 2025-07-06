package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapperImpl
import com.maksimowiczm.foodyou.feature.food.ui.product.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.food.ui.product.update.UpdateProductScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodModule = module {
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::UpdateProductScreenViewModel)

    factoryOf(::ProductMapperImpl).bind<ProductMapper>()
}
