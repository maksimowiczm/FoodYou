package com.maksimowiczm.foodyou.feature.addfood

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.NavigationFeature
import com.maksimowiczm.foodyou.feature.addfood.PortionFeature.Companion.navigateToPortion
import com.maksimowiczm.foodyou.feature.addfood.PortionFeature.Companion.popPortion
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.ui.AddFoodViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchHome
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchViewModel
import com.maksimowiczm.foodyou.feature.camera.CameraFeature
import com.maksimowiczm.foodyou.feature.camera.CameraFeature.Companion.navigateToBarcodeScanner
import com.maksimowiczm.foodyou.feature.camera.CameraFeature.Companion.popBarcodeScanner
import com.maksimowiczm.foodyou.feature.product.ProductFeature
import com.maksimowiczm.foodyou.feature.product.ProductFeature.Companion.navigateToProducts
import com.maksimowiczm.foodyou.feature.product.ProductFeature.Companion.popProducts
import com.maksimowiczm.foodyou.feature.setup
import com.maksimowiczm.foodyou.navigation.ForwardBackwardComposableDefaults
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.ui.motion.crossfadeOut
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

abstract class AddFoodFeature(
    private val addFoodRepository: Module.() -> KoinDefinition<AddFoodRepository>,
    private val cameraFeature: CameraFeature,
    val productFeature: ProductFeature,
    private val portionFeature: PortionFeature = PortionFeature(
        addFoodRepository = addFoodRepository,
        productFeature = productFeature
    )
) : Feature.Koin,
    NavigationFeature<AddFoodFeature.GraphProps> {
    final override fun KoinApplication.setup() {
        modules(
            module {
                viewModelOf(::AddFoodViewModel)
                viewModelOf(::SearchViewModel)

                addFoodRepository().bind()
            }
        )

        setup(
            cameraFeature,
            productFeature,
            portionFeature
        )
    }

    data class GraphProps(val onClose: () -> Unit, val onSearchSettings: (() -> Unit)?)

    @Serializable
    data object Search

    final override fun NavGraphBuilder.graph(navController: NavController, props: GraphProps) {
        navigation<Route>(
            startDestination = Search
        ) {
            forwardBackwardComposable<Search>(
                exitTransition = {
                    if (initialState.destination.hasRoute<PortionFeature.Route>()) {
                        ForwardBackwardComposableDefaults.exitTransition()
                    } else {
                        crossfadeOut()
                    }
                },
                popEnterTransition = {
                    if (initialState.destination.hasRoute<PortionFeature.Route>()) {
                        ForwardBackwardComposableDefaults.popEnterTransition()
                    } else {
                        crossfadeIn()
                    }
                }
            ) {
                val viewModel = it.sharedViewModel<SearchViewModel>(navController)

                SearchHome(
                    animatedVisibilityScope = this,
                    onProductClick = { epochDay, mealId, id ->
                        navController.navigateToPortion(
                            route = PortionFeature.Route(
                                epochDay = epochDay,
                                meal = mealId,
                                productId = id
                            ),
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    },
                    onSearchSettings = props.onSearchSettings,
                    onBack = props.onClose,
                    onCreateProduct = { epochDay, mealId ->
                        navController.navigateToProducts(
                            route = ProductFeature.CreateProduct(
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
                        onBack = { navController.popPortion() },
                        onSuccess = { navController.popPortion() },
                        onProductEdit = { id ->
                            navController.navigateToProducts(
                                route = ProductFeature.UpdateProduct(
                                    productId = id
                                )
                            )
                        },
                        onProductDelete = { navController.popPortion() }
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
                            navController.popProducts<ProductFeature.CreateProduct>()
                        },
                        createOnSuccess = { productId, epochDay, mealId ->
                            navController.navigateToPortion(
                                route = PortionFeature.Route(
                                    epochDay = epochDay,
                                    meal = mealId,
                                    productId = productId
                                ),
                                navOptions = navOptions {
                                    popUpTo(Route) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            )
                        },
                        updateOnNavigateBack = {
                            navController.popProducts<ProductFeature.UpdateProduct>()
                        },
                        updateOnSuccess = {
                            navController.popProducts<ProductFeature.UpdateProduct>()
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
