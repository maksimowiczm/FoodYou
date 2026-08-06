package com.maksimowiczm.foodyou.features.userproduct

import com.maksimowiczm.foodyou.features.userproduct.create.CreateProductViewModel
import com.maksimowiczm.foodyou.features.userproduct.edit.EditProductViewModel
import com.maksimowiczm.foodyou.shared.ui.utility.FoodNameSelector
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productModule = module {
    viewModelOf(::CreateProductViewModel)
    scope<CreateProductViewModel> { scopedOf(::ProductFormTransformer) }

    viewModelOf(::EditProductViewModel)
    scope<EditProductViewModel> {
        scoped { get<FoodNameSelector>() }
        scopedOf(::ProductFormTransformer)
    }

    factoryOf(::ProductFormTransformer)
}
