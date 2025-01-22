package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepositoryImpl
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.PortionViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.CameraBarcodeScannerViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val addFoodModule = module {
    viewModelOf(::SearchViewModel)
    viewModelOf(::PortionViewModel)
    viewModelOf(::CameraBarcodeScannerViewModel)

    factoryOf(::AddFoodRepositoryImpl).bind<AddFoodRepository>()
}
