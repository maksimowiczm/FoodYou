package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.addfood.ui.camera.BarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.camera.zxingCameraBarcodeScannerScreen
import org.koin.dsl.bind
import org.koin.dsl.module

val flavourModule = module {
    single {
        zxingCameraBarcodeScannerScreen
    }.bind<BarcodeScannerScreen>()
}
