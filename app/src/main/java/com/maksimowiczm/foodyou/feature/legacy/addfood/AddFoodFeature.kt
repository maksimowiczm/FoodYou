package com.maksimowiczm.foodyou.feature.legacy.addfood

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.legacy.addfood.PortionFeature.Companion.navigateToPortion
import com.maksimowiczm.foodyou.feature.legacy.addfood.PortionFeature.Companion.popPortion
import com.maksimowiczm.foodyou.feature.legacy.addfood.ui.search.SearchHome
import com.maksimowiczm.foodyou.feature.legacy.addfood.ui.search.SearchViewModel
import com.maksimowiczm.foodyou.feature.legacy.camera.CameraFeature
import com.maksimowiczm.foodyou.feature.legacy.camera.CameraFeature.Companion.navigateToBarcodeScanner
import com.maksimowiczm.foodyou.feature.legacy.camera.CameraFeature.Companion.popBarcodeScanner
import com.maksimowiczm.foodyou.feature.legacy.product.ProductFeature
import com.maksimowiczm.foodyou.feature.legacy.product.ProductFeature.Companion.navigateToProducts
import com.maksimowiczm.foodyou.feature.legacy.product.ProductFeature.Companion.popProducts
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.ui.ext.sharedViewModel
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.ui.motion.crossfadeOut
import kotlinx.serialization.Serializable
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

abstract class AddFoodFeature(
    private val addFoodRepository: Module.() -> KoinDefinition<AddFoodRepository>,
    private val cameraFeature: CameraFeature,
    val productFeature: ProductFeature,
    private val portionFeature: PortionFeature = PortionFeature(
        addFoodRepository = addFoodRepository,
        productFeature = productFeature
    )
) {

    data class GraphProps(val onClose: () -> Unit)

    @Serializable
    data object Search

    fun NavGraphBuilder.graph(navController: NavController, props: GraphProps) {
        navigation<Route>(
            startDestination = Search
        ) {
            forwardBackwardComposable<Search>(
                exitTransition = { crossfadeOut() },
                popEnterTransition = { crossfadeIn() }
            ) {
                val viewModel = it.sharedViewModel<SearchViewModel>(navController)

                SearchHome(
                    onProductClick = { epochDay, mealId, id ->
                        navController.navigateToPortion(
                            route = PortionFeature.Create(
                                epochDay = epochDay,
                                mealId = mealId,
                                productId = id
                            ),
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    },
                    onBack = props.onClose,
                    onCreateProduct = { epochDay, mealId ->
                        navController.navigateToProducts(
                            route = ProductFeature.CreateProductDialog(
                                epochDay = epochDay,
                                mealId = mealId
                            ),
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    },
                    onBarcodeScanner = {
                        navController.navigateToBarcodeScanner(
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    },
                    viewModel = viewModel
                )
            }
            with(portionFeature) {
                graph(
                    navController = navController,
                    props = PortionFeature.GraphProps(
                        create = PortionFeature.PortionScreenProps(
                            onBack = { navController.popPortion<PortionFeature.Create>() },
                            onSuccess = { navController.popPortion<PortionFeature.Create>() },
                            onProductEdit = { id ->
                                navController.navigateToProducts(
                                    route = ProductFeature.UpdateProductDialog(
                                        productId = id
                                    )
                                )
                            },
                            onProductDelete = { navController.popPortion<PortionFeature.Create>() }
                        ),
                        edit = PortionFeature.PortionScreenProps(
                            onBack = { navController.popPortion<PortionFeature.Edit>() },
                            onSuccess = { navController.popPortion<PortionFeature.Edit>() },
                            onProductEdit = { id ->
                                navController.navigateToProducts(
                                    route = ProductFeature.UpdateProductDialog(
                                        productId = id
                                    )
                                )
                            },
                            onProductDelete = { navController.popPortion<PortionFeature.Edit>() }
                        )
                    )
                )
            }
            with(cameraFeature) {
                val handlerFactory = CameraFeature.BarcodeHandlerFactory {
                    val viewModel = sharedViewModel<SearchViewModel>(navController)
                    val hapticFeedback = LocalHapticFeedback.current
                    CameraFeature.BarcodeHandler {
                        viewModel.onSearch(it)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                        navController.popBarcodeScanner()
                    }
                }

                graph(
                    navController = navController,
                    props = CameraFeature.GraphProps(
                        handlerFactory = handlerFactory
                    )
                )
            }
            with(productFeature) {
                graph(
                    navController = navController,
                    props = ProductFeature.GraphProps(
                        createOnNavigateBack = {
                            navController.popProducts<ProductFeature.CreateProductDialog>()
                        },
                        createOnSuccess = { productId, epochDay, mealId ->
                            navController.navigateToPortion(
                                route = PortionFeature.Create(
                                    epochDay = epochDay,
                                    mealId = mealId,
                                    productId = productId
                                ),
                                navOptions = navOptions {
                                    popUpTo<Route> {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            )
                        },
                        updateOnNavigateBack = {
                            navController.popProducts<ProductFeature.UpdateProductDialog>()
                        },
                        updateOnSuccess = {
                            navController.popProducts<ProductFeature.UpdateProductDialog>()
                        }
                    )
                )
            }
        }
    }

    @Serializable
    data class Route(val epochDay: Int, val mealId: Long)

    companion object {
        fun NavController.navigateToAddFood(route: Route, navOptions: NavOptions? = null) {
            navigate(route, navOptions)
        }

        fun NavController.popAddFood(inclusive: Boolean = true, saveState: Boolean = false) {
            popBackStack<Route>(
                inclusive = inclusive,
                saveState = saveState
            )
        }
    }
}
