package com.maksimowiczm.foodyou.core.feature.addfood.ui

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.maksimowiczm.foodyou.core.feature.addfood.ui.portion.PortionScreen
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchHome
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.rememberSearchListState
import com.maksimowiczm.foodyou.core.feature.camera.navigation.BarcodeScannerRoute
import com.maksimowiczm.foodyou.core.feature.camera.navigation.cameraGraph
import com.maksimowiczm.foodyou.core.feature.camera.navigation.navigateToBarcodeScanner
import com.maksimowiczm.foodyou.core.feature.product.navigation.ProductsRoute
import com.maksimowiczm.foodyou.core.feature.product.navigation.navigateToProducts
import com.maksimowiczm.foodyou.core.feature.product.navigation.productsGraph
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddFoodScreen(
    onClose: () -> Unit,
    onSearchSettings: () -> Unit,
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
        addFoodState.searchTopBarState.recentQueries = recentQueries
    }

    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    LaunchedEffect(totalCalories) {
        addFoodState.searchBottomBarState.totalCalories = totalCalories
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
                onSearchSettings = onSearchSettings,
                onSearch = {
                    viewModel.onSearch(
                        query = it,
                        localOnly = false
                    )
                },
                onClearSearch = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    viewModel.onClearSearch()
                },
                onRetry = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    viewModel.onRetry()
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
                onCreateProduct = {
                    navController.navigateToProducts(
                        route = ProductsRoute.CreateProduct(
                            epochDay = viewModel.date.toEpochDays(),
                            mealId = viewModel.mealId
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
        cameraGraph(
            onBarcodeScan = {
                viewModel.onBarcodeScan(it)

                addFoodState.searchTopBarState.textFieldState.setTextAndPlaceCursorAtEnd(it)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)

                navController.popBackStack(
                    route = BarcodeScannerRoute,
                    inclusive = true
                )
            }
        )
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
private data object Portion
