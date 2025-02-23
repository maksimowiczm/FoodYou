package com.maksimowiczm.foodyou.feature.camera

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.NavigationFeature
import com.maksimowiczm.foodyou.feature.camera.ui.BarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.camera.ui.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.camera.ui.CameraBarcodeScannerViewModel
import com.maksimowiczm.foodyou.feature.camera.ui.CameraSharedTransitionKeys
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.ui.LocalSharedTransitionScope
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.ui.motion.crossfadeOut
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

abstract class CameraFeature(
    barcodeScannerScreen: Module.() -> KoinDefinition<BarcodeScannerScreen>
) : Feature.Koin,
    NavigationFeature<CameraFeature.GraphProps> {
    private val module = module {
        viewModelOf(::CameraBarcodeScannerViewModel)
        barcodeScannerScreen().bind()
    }

    final override fun KoinApplication.setup() {
        modules(module)
    }

    fun interface BarcodeHandlerFactory {
        @Composable
        operator fun NavBackStackEntry.invoke(): BarcodeHandler
    }

    fun interface BarcodeHandler {
        fun onBarcodeScan(barcode: String)
    }

    data class GraphProps(val handlerFactory: BarcodeHandlerFactory)

    @OptIn(ExperimentalSharedTransitionApi::class)
    final override fun NavGraphBuilder.graph(navController: NavController, props: GraphProps) {
        crossfadeComposable<BarcodeScannerRoute> {
            val sharedTransitionScope =
                LocalSharedTransitionScope.current ?: error("No shared transition scope found")

            val handler = with(props.handlerFactory) {
                it.invoke()
            }

            with(sharedTransitionScope) {
                CameraBarcodeScannerScreen(
                    onBarcodeScan = handler::onBarcodeScan,
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                CameraSharedTransitionKeys.BARCODE_SCANNER
                            ),
                            animatedVisibilityScope = this@crossfadeComposable,
                            enter = crossfadeIn(),
                            exit = crossfadeOut(),
                            clipInOverlayDuringTransition = OverlayClip(
                                MaterialTheme.shapes.large
                            )
                        )
                        .skipToLookaheadSize()
                )
            }
        }
    }

    @Serializable
    data object BarcodeScannerRoute

    companion object {
        fun NavController.navigateToBarcodeScanner(navOptions: NavOptions? = null) {
            navigate(
                route = BarcodeScannerRoute,
                navOptions = navOptions
            )
        }

        fun NavController.popBarcodeScanner(inclusive: Boolean = true, saveState: Boolean = false) {
            popBackStack<BarcodeScannerRoute>(
                inclusive = inclusive,
                saveState = saveState
            )
        }
    }
}
