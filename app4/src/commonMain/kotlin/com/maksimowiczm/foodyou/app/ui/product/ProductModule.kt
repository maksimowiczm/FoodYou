package com.maksimowiczm.foodyou.app.ui.product

import com.maksimowiczm.foodyou.app.ui.product.create.CreateProductViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val productModule = module { viewModelOf(::CreateProductViewModel) }
