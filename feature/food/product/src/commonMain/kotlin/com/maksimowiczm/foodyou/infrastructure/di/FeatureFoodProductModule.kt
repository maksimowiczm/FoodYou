package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.product.presentation.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.food.product.presentation.download.DownloadProductViewModel
import com.maksimowiczm.foodyou.feature.food.product.presentation.update.UpdateProductViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureFoodProductModule = module {
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::UpdateProductViewModel)
    viewModel { (text: String?) -> DownloadProductViewModel(text, get()) }
}
