package com.maksimowiczm.foodyou.feature.addfood.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.PortionScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.PortionViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchHome
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchViewModel
import com.maksimowiczm.foodyou.feature.camera.navigation.BarcodeScannerRoute
import com.maksimowiczm.foodyou.feature.camera.navigation.cameraGraph
import com.maksimowiczm.foodyou.feature.camera.navigation.navigateToBarcodeScanner
import com.maksimowiczm.foodyou.feature.product.navigation.ProductsRoute
import com.maksimowiczm.foodyou.feature.product.navigation.navigateToProducts
import com.maksimowiczm.foodyou.feature.product.navigation.productsGraph
import com.maksimowiczm.foodyou.navigation.ForwardBackwardComposableDefaults
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.ui.motion.crossfadeOut
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Suppress("ktlint:compose:vm-forwarding-check")
@Composable
fun AddFoodScreen(
    onClose: () -> Unit,
    onSearchSettings: (() -> Unit)?,
    modifier: Modifier = Modifier,
    addFoodViewModel: AddFoodViewModel = koinViewModel(),
    searchViewModel: SearchViewModel = koinViewModel(),
    portionViewModel: PortionViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val hapticFeedback = LocalHapticFeedback.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Home
    ) {
        forwardBackwardComposable<Home>(
            exitTransition = {
                if (initialState.destination.hasRoute<Portion>()) {
                    ForwardBackwardComposableDefaults.exitTransition()
                } else {
                    crossfadeOut()
                }
            },
            popEnterTransition = {
                if (initialState.destination.hasRoute<Portion>()) {
                    ForwardBackwardComposableDefaults.popEnterTransition()
                } else {
                    crossfadeIn()
                }
            }
        ) {
            SearchHome(
                animatedVisibilityScope = this,
                viewModel = searchViewModel,
                onProductClick = { id ->
                    portionViewModel.loadProduct(id)

                    navController.navigate(
                        route = Portion,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onSearchSettings = onSearchSettings,
                onBack = onClose,
                onCreateProduct = {
                    navController.navigateToProducts(
                        route = ProductsRoute.CreateProduct(
                            epochDay = searchViewModel.date.toEpochDays(),
                            mealId = searchViewModel.mealId
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
                }
            )
        }
        forwardBackwardComposable<Portion> {
            PortionScreen(
                onBack = {
                    navController.popBackStack<Portion>(
                        inclusive = true
                    )
                },
                onSuccess = {
                    navController.popBackStack<Home>(
                        inclusive = false
                    )
                },
                onEditClick = {
                    navController.navigateToProducts(
                        route = ProductsRoute.UpdateProduct(
                            productId = it
                        )
                    )
                },
                onDeleteClick = {
                    navController.popBackStack<Portion>(
                        inclusive = true
                    )

                    addFoodViewModel.onProductDelete(it)
                },
                viewModel = portionViewModel
            )
        }
        cameraGraph(
            onBarcodeScan = {
                searchViewModel.onSearch(it)

                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)

                navController.popBackStack<BarcodeScannerRoute>(
                    inclusive = true
                )
            }
        )
        productsGraph(
            createOnNavigateBack = {
                navController.popBackStack<ProductsRoute.CreateProduct>(
                    inclusive = true
                )
            },
            createOnSuccess = { productId, _, _ ->
                portionViewModel.loadProduct(productId)

                navController.navigate(
                    route = Portion,
                    navOptions = navOptions {
                        popUpTo(Home) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                )
            },
            updateOnNavigateBack = {
                navController.popBackStack<ProductsRoute.UpdateProduct>(
                    inclusive = true
                )
            },
            updateOnSuccess = {
                navController.popBackStack<ProductsRoute.UpdateProduct>(
                    inclusive = true
                )
            }
        )
    }
}

@Serializable
private data object Home

@Serializable
private data object Portion
