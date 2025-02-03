package com.maksimowiczm.foodyou.core.feature.camera

import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.camera.ui.CameraBarcodeScannerViewModel
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * CameraFeature is navigation feature that provides a camera barcode scanner. Should be used with
 * [com.maksimowiczm.foodyou.core.feature.camera.navigation.cameraGraph].
 */
object CameraFeature : Feature.Koin {
    override fun KoinApplication.setup() {
        modules(
            module {
                viewModelOf(::CameraBarcodeScannerViewModel)
            }
        )
    }
}
