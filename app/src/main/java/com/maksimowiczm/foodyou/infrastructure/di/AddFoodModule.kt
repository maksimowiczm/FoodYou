package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepositoryImpl
import com.maksimowiczm.foodyou.feature.addfood.ui.AddFoodViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.camera.CameraBarcodeScannerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val addFoodModule = module {
    viewModelOf(::CameraBarcodeScannerViewModel)
    viewModelOf(::AddFoodViewModel)

    factory {
        AddFoodRepositoryImpl(
            addFoodDatabase = get(),
            productDatabase = get(),
            remoteProductDatabase = get()
        )
    }.bind<AddFoodRepository>()
}
