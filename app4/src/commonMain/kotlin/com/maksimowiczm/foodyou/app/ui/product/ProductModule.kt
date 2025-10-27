package com.maksimowiczm.foodyou.app.ui.product

import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.app.ui.product.create.CreateProductViewModel
import com.maksimowiczm.foodyou.app.ui.product.edit.EditProductViewModel
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productModule = module {
    viewModelOf(::CreateProductViewModel)
    scope<CreateProductViewModel> {
        scopedOf(::ObservePrimaryAccountUseCase)
        scopedOf(::ProductFormTransformer)
    }

    viewModelOf(::EditProductViewModel)
    scope<EditProductViewModel> {
        scopedOf(::ObservePrimaryAccountUseCase)
        scoped { get<FoodNameSelector>() }
        scopedOf(::ProductFormTransformer)
    }

    factoryOf(::ProductFormTransformer)
}
