package com.maksimowiczm.foodyou.feature.camera

import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.camera.ui.BarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.camera.ui.CameraBarcodeScannerViewModel
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * CameraFeature is navigation feature that provides a camera barcode scanner. Should be used with
 * [com.maksimowiczm.foodyou.feature.camera.navigation.cameraGraph].
 */
abstract class CameraFeature(
    barcodeScannerScreen: Module.() -> KoinDefinition<BarcodeScannerScreen>
) : Feature.Koin {
    private val module = module {
        viewModelOf(::CameraBarcodeScannerViewModel)
        barcodeScannerScreen().bind()
    }

    final override fun KoinApplication.setup() {
        modules(module)
    }
}
