package com.maksimowiczm.foodyou.core.infrastructure.di

import com.maksimowiczm.foodyou.core.feature.camera.ui.BarcodeScannerScreen
import com.maksimowiczm.foodyou.core.feature.camera.zxingCameraBarcodeScannerScreen
import org.koin.dsl.bind
import org.koin.dsl.module

val flavourModule = module {
    factory {
        zxingCameraBarcodeScannerScreen
    }.bind<BarcodeScannerScreen>()
}
