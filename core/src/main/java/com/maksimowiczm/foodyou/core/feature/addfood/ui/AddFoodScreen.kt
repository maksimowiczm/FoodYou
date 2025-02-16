package com.maksimowiczm.foodyou.core.feature.addfood.ui

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.core.feature.addfood.ui.portion.PortionScreen
import com.maksimowiczm.foodyou.core.feature.addfood.ui.portion.PortionViewModel
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchHome
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchViewModel
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.rememberSearchListState
import com.maksimowiczm.foodyou.core.feature.camera.navigation.BarcodeScannerRoute
import com.maksimowiczm.foodyou.core.feature.camera.navigation.cameraGraph
import com.maksimowiczm.foodyou.core.feature.camera.navigation.navigateToBarcodeScanner
import com.maksimowiczm.foodyou.core.feature.product.navigation.ProductsRoute
import com.maksimowiczm.foodyou.core.feature.product.navigation.navigateToProducts
import com.maksimowiczm.foodyou.core.feature.product.navigation.productsGraph
import com.maksimowiczm.foodyou.core.navigation.ForwardBackwardComposableDefaults
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.ui.motion.crossfadeIn
import com.maksimowiczm.foodyou.core.ui.motion.crossfadeOut
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddFoodScreen(
    onClose: () -> Unit,
    onSearchSettings: () -> Unit,
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = koinViewModel(),
    portionViewModel: PortionViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val hapticFeedback = LocalHapticFeedback.current

    val addFoodState = rememberAddFoodState(
        searchListState = rememberSearchListState(
            onQuickAdd = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                searchViewModel.onQuickAdd(it)
            },
            onQuickRemove = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                searchViewModel.onQuickRemove(it)
            }
        ),
        navController = navController
    )

    val queryState by searchViewModel.queryState.collectAsStateWithLifecycle()
    LaunchedEffect(queryState) {
        addFoodState.searchListState.onQueryResultChange(queryState)
    }

    val recentQueries by searchViewModel.recentQueries.collectAsStateWithLifecycle()
    LaunchedEffect(recentQueries) {
        addFoodState.searchTopBarState.recentQueries = recentQueries
    }

    val totalCalories by searchViewModel.totalCalories.collectAsStateWithLifecycle()
    LaunchedEffect(totalCalories) {
        addFoodState.searchBottomBarState.totalCalories = totalCalories
    }

    NavHost(
        modifier = modifier,
        navController = addFoodState.navController,
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
                addFoodState = addFoodState,
                onSearchSettings = onSearchSettings,
                onSearch = {
                    searchViewModel.onSearch(
                        query = it,
                        localOnly = false
                    )
                },
                onClearSearch = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    searchViewModel.onClearSearch()
                },
                onRetry = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    searchViewModel.onRetry()
                },
                onBack = onClose,
                onProductClick = {
                    portionViewModel.loadProduct(it.model.product.id)

                    navController.navigate(
                        route = Portion,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onProductLongClick = {
                    portionViewModel.loadProduct(it.model.product.id)

                    navController.navigateToProducts(
                        route = ProductsRoute.UpdateProduct(
                            productId = it.model.product.id
                        ),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onCreateProduct = {
                    navController.navigateToProducts(
                        route = ProductsRoute.CreateProduct(
                            epochDay = searchViewModel.date.toEpochDays(),
                            mealId = searchViewModel.mealId
                        )
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
                    searchViewModel.onSearch(
                        query = searchViewModel.query,
                        localOnly = true,
                        persistError = true
                    )

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

                    searchViewModel.onProductDelete(it)
                },
                viewModel = portionViewModel
            )
        }
        cameraGraph(
            onBarcodeScan = {
                searchViewModel.onBarcodeScan(it)

                addFoodState.searchTopBarState.textFieldState.setTextAndPlaceCursorAtEnd(it)
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
