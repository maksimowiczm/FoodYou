package com.maksimowiczm.foodyou.feature.camera

object OpenSourceCameraFeature : CameraFeature(
    barcodeScannerScreen = { factory { zxingCameraBarcodeScannerScreen } }
)
