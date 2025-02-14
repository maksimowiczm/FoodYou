package com.maksimowiczm.foodyou.core.feature.camera.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.maksimowiczm.foodyou.core.feature.camera.ui.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.core.feature.camera.ui.CameraSharedTransitionKeys
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.ui.LocalSharedTransitionScope
import com.maksimowiczm.foodyou.core.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.core.ui.motion.crossfadeOut
import kotlinx.serialization.Serializable

@Serializable
data object BarcodeScannerRoute

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.cameraGraph(
    onBarcodeScan: (String) -> Unit
) {
    crossfadeComposable<BarcodeScannerRoute>() {
        val sharedTransitionScope =
            LocalSharedTransitionScope.current ?: error("No shared transition scope found")

        with(sharedTransitionScope) {
            CameraBarcodeScannerScreen(
                onBarcodeScan = onBarcodeScan,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            CameraSharedTransitionKeys.BARCODE_SCANNER
                        ),
                        animatedVisibilityScope = this@crossfadeComposable,
                        enter = crossfadeIn(),
                        exit = crossfadeOut(),
                        clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.large)
                    )
                    .skipToLookaheadSize()
            )
        }
    }
}

fun NavController.navigateToBarcodeScanner(
    navOptions: NavOptions? = null
) {
    navigate(
        route = BarcodeScannerRoute,
        navOptions = navOptions
    )
}
