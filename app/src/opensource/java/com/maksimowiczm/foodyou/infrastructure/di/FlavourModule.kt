package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.addfood.ui.search.BarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.zxingCameraBarcodeScannerScreen
import org.koin.dsl.bind
import org.koin.dsl.module

val flavourModule = module {
    single {
        zxingCameraBarcodeScannerScreen
    }.bind<BarcodeScannerScreen>()
}
