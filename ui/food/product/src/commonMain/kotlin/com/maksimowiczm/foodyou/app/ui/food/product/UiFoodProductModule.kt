package com.maksimowiczm.foodyou.app.ui.food.product

import com.maksimowiczm.foodyou.app.ui.food.product.create.CreateProductViewModel
import com.maksimowiczm.foodyou.app.ui.food.product.download.DownloadProductHolder
import com.maksimowiczm.foodyou.app.ui.food.product.download.DownloadProductViewModel
import com.maksimowiczm.foodyou.app.ui.food.product.update.UpdateProductViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiFoodProductModule = module {
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::UpdateProductViewModel)
    viewModel { (text: String?, holder: DownloadProductHolder) ->
        DownloadProductViewModel(text, get(), holder)
    }
    viewModelOf(::DownloadProductHolder)
}
