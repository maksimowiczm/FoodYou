package com.maksimowiczm.foodyou.feature.addfood.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.addfood.ui.camera.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.PortionScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchHome
import com.maksimowiczm.foodyou.feature.addfood.ui.search.rememberSearchListState
import com.maksimowiczm.foodyou.feature.product.navigation.ProductsRoute
import com.maksimowiczm.foodyou.feature.product.navigation.navigateToProducts
import com.maksimowiczm.foodyou.feature.product.navigation.productsGraph
import com.maksimowiczm.foodyou.ui.LocalSharedTransitionScope
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AddFoodScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddFoodViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val hapticFeedback = LocalHapticFeedback.current

    val addFoodState = rememberAddFoodState(
        searchListState = rememberSearchListState(
            onQuickAdd = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                viewModel.onQuickAdd(it)
            },
            onQuickRemove = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                viewModel.onQuickRemove(it)
            }
        ),
        navController = navController
    )

    val queryState by viewModel.queryState.collectAsStateWithLifecycle()
    LaunchedEffect(queryState) {
        addFoodState.searchListState.onQueryResultChange(queryState)
    }

    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()
    LaunchedEffect(recentQueries) {
        addFoodState.searchBarState.recentQueries = recentQueries
    }

    NavHost(
        modifier = modifier,
        navController = addFoodState.navController,
        startDestination = Home
    ) {
        composable<Home>(
            popEnterTransition = {
                var transition = fadeIn(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = LinearOutSlowInEasing
                    )
                )

                val topDestination = initialState.destination.hierarchy.first()
                val initialIsPortion = topDestination.hierarchy.any { it.hasRoute<Portion>() }

                if (initialIsPortion) {
                    transition += slideInHorizontally(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = LinearOutSlowInEasing
                        )
                    ) {
                        it / 2
                    }
                }

                transition
            },
            exitTransition = {
                scaleOut(
                    targetScale = .5f
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 100,
                        easing = FastOutLinearInEasing
                    )
                )
            }
        ) {
            SearchHome(
                animatedVisibilityScope = this,
                addFoodState = addFoodState,
                onSearch = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    viewModel.onSearch(
                        query = it,
                        localOnly = false
                    )
                },
                onBack = onClose,
                onProductClick = {
                    viewModel.onLoadProduct(it.model.product.id)

                    navController.navigate(
                        route = Portion,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onRetry = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    viewModel.onRetry()
                },
                onBarcodeScanner = {
                    navController.navigate(
                        route = BarcodeScanner,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onCreateProduct = {
                    navController.navigateToProducts(
                        route = ProductsRoute.CreateProduct(
                            epochDay = viewModel.date.toEpochDay(),
                            mealType = viewModel.meal
                        )
                    )
                }
            )
        }
        composable<BarcodeScanner> {
            val sharedTransitionScope =
                LocalSharedTransitionScope.current ?: error("No shared transition scope found")

            with(sharedTransitionScope) {
                CameraBarcodeScannerScreen(
                    onBarcodeScan = {
                        viewModel.onBarcodeScan(it)

                        addFoodState.searchBarState.textFieldState.setTextAndPlaceCursorAtEnd(it)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)

                        navController.popBackStack(
                            route = BarcodeScanner,
                            inclusive = true
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                AddFoodSharedTransitionKeys.BARCODE_SCANNER
                            ),
                            animatedVisibilityScope = this@composable
                        )
                )
            }
        }
        composable<Portion>(
            enterTransition = {
                scaleIn(
                    initialScale = .65f,
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = LinearOutSlowInEasing
                    )
                ) + fadeIn(
                    tween(
                        durationMillis = 250,
                        easing = LinearOutSlowInEasing
                    )
                )
            },
            exitTransition = {
                scaleOut(
                    targetScale = .8f,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutLinearInEasing
                    )
                ) + slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutLinearInEasing
                    ),
                    targetOffsetX = { -it / 2 }
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutLinearInEasing
                    )
                )
            }
        ) {
            val uiState by viewModel.productState.collectAsStateWithLifecycle()

            PortionScreen(
                uiState = uiState,
                onSuccess = {
                    navController.popBackStack(
                        route = Portion,
                        inclusive = true
                    )
                },
                onSave = viewModel::onAddPortion,
                onNavigateBack = {
                    navController.popBackStack(
                        route = Portion,
                        inclusive = true
                    )
                }
            )
        }
        productsGraph(
            createOnSuccess = { productId, _, _ ->
                viewModel.onLoadProduct(productId)

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
            createOnNavigateBack = {
                navController.popBackStack(
                    route = Home,
                    inclusive = false
                )
            }
        )
    }
}

@Serializable
private data object Home

@Serializable
private data object BarcodeScanner

@Serializable
private data object Portion
