package com.maksimowiczm.foodyou.app.ui.userfood

import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelector
import com.maksimowiczm.foodyou.app.ui.userfood.create.CreateProductViewModel
import com.maksimowiczm.foodyou.app.ui.userfood.edit.EditProductViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
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
    factoryOf(::GetAppAccountEnergyUnitUseCaseImpl).bind<GetAppAccountEnergyUnitUseCase>()
}
