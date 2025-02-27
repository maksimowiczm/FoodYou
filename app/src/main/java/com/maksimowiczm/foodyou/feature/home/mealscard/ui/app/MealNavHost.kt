package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.meal.DiaryDayMealScreen
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement.CreateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement.MeasurementScreen
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement.UpdateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.create.CreateProductDialog
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.update.UpdateProductDialog
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search.SearchHome
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search.SearchViewModel
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
private data object MealHome

@Serializable
private data object Search

@Serializable
private data class CreateMeasurement(val productId: Long)

@Serializable
private data class EditMeasurement(val measurementId: Long)

@Serializable
private data object BarcodeScanner

@Serializable
private data object CreateProductDialog

@Serializable
private data class EditProductDialog(val productId: Long)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MealNavHost(
    outerScope: AnimatedVisibilityScope,
    mealId: Long,
    epochDay: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    skipToSearchScreen: Boolean = false
) {
    val date = LocalDate.fromEpochDays(epochDay)
    val searchViewModel = koinViewModel<SearchViewModel>(
        parameters = { parametersOf(date, mealId) }
    )
    val lazyListState = rememberLazyListState()

    val startDestination: Any = if (skipToSearchScreen) Search else MealHome

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Fade in and fade out on fab clicks
        crossfadeComposable<MealHome>(
            popEnterTransition = {
                if (initialState.destination.hasRoute<Search>()) {
                    fadeIn()
                } else {
                    crossfadeIn()
                }
            },
            exitTransition = { fadeOut() }
        ) {
            DiaryDayMealScreen(
                date = date,
                mealId = mealId,
                navigationScope = this,
                mealHeaderScope = outerScope,
                onProductAdd = {
                    navController.navigate(
                        route = Search,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onBarcodeScan = {
                    navController.navigate(
                        route = Search,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                    navController.navigate(
                        route = BarcodeScanner,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onEditEntry = {
                    navController.navigate(
                        route = EditMeasurement(it),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                }
            )
        }
        crossfadeComposable<Search>(
            popEnterTransition = {
                // Delay fade in to mimic scrim effect on dialog close
                if (initialState.destination.hasRoute<CreateProductDialog>()) {
                    fadeIn(
                        tween(
                            delayMillis = 50
                        )
                    )
                } else {
                    crossfadeIn()
                }
            }
        ) {
            val sharedTransitionScope =
                LocalMealSharedTransitionScope.current ?: error("No shared transition scope found")

            with(sharedTransitionScope) {
                SearchHome(
                    onProductClick = {
                        navController.navigate(
                            route = CreateMeasurement(it),
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    },
                    onBack = { navController.popBackStack<Search>(inclusive = true) },
                    onCreateProduct = {
                        navController.navigate(
                            route = CreateProductDialog,
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    },
                    onBarcodeScanner = {
                        navController.navigate(
                            route = BarcodeScanner,
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    },
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = SearchSharedTransition.CONTAINER
                            ),
                            enter = SearchSharedTransition.screenContainerEnterTransition,
                            exit = SearchSharedTransition.screenContainerExitTransition,
                            animatedVisibilityScope = this@crossfadeComposable
                        )
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = SearchSharedTransition.CONTENT
                            ),
                            animatedVisibilityScope = this@crossfadeComposable,
                            enter = SearchSharedTransition.screenContentEnterTransition,
                            exit = SearchSharedTransition.screenContentExitTransition
                        ),
                    viewModel = searchViewModel,
                    lazyListState = lazyListState
                )
            }
        }
        crossfadeComposable<BarcodeScanner> {
            CameraBarcodeScannerScreen(
                onBarcodeScan = {
                    searchViewModel.onSearch(it)

                    navController.navigate(
                        route = Search,
                        navOptions {
                            launchSingleTop = true

                            popUpTo<BarcodeScanner> {
                                inclusive = true
                            }
                        }
                    )
                },
                onClose = { navController.popBackStack<BarcodeScanner>(inclusive = true) },
                modifier = Modifier.fillMaxSize()
            )
        }
        crossfadeComposable<CreateMeasurement>(
            popEnterTransition = {
                // Delay fade in to mimic scrim effect on dialog close
                if (initialState.destination.hasRoute<EditProductDialog>()) {
                    fadeIn(
                        tween(
                            delayMillis = 50
                        )
                    )
                } else {
                    crossfadeIn()
                }
            }
        ) {
            val (productId) = it.toRoute<CreateMeasurement>()

            MeasurementScreen(
                onBack = { navController.popBackStack<CreateMeasurement>(inclusive = true) },
                onSuccess = { navController.popBackStack<CreateMeasurement>(inclusive = true) },
                onEditClick = { id ->
                    navController.navigate(
                        route = EditProductDialog(id),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onDelete = { navController.popBackStack<CreateMeasurement>(inclusive = true) },
                viewModel = koinViewModel<CreateMeasurementViewModel>(
                    parameters = { parametersOf(mealId, date, productId) }
                )
            )
        }
        crossfadeComposable<EditMeasurement>(
            popEnterTransition = {
                // Delay fade in to simulate scrim effect on dialog close
                if (initialState.destination.hasRoute<EditProductDialog>()) {
                    fadeIn(
                        tween(
                            delayMillis = 50
                        )
                    )
                } else {
                    crossfadeIn()
                }
            }
        ) {
            val (measurementId) = it.toRoute<EditMeasurement>()

            MeasurementScreen(
                onBack = { navController.popBackStack<EditMeasurement>(inclusive = true) },
                onSuccess = { navController.popBackStack<EditMeasurement>(inclusive = true) },
                onEditClick = { id ->
                    navController.navigate(
                        route = EditProductDialog(id),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onDelete = { navController.popBackStack<EditMeasurement>(inclusive = true) },
                viewModel = koinViewModel<UpdateMeasurementViewModel>(
                    parameters = { parametersOf(measurementId) }
                )
            )
        }
        // Slide beyond the screen on enter and exit
        composable<CreateProductDialog>(
            enterTransition = {
                crossfadeIn() + slideInVertically(
                    animationSpec = tween(
                        easing = LinearOutSlowInEasing
                    ),
                    initialOffsetY = { it }
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    ),
                    targetOffsetY = { it }
                ) + scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    )
                )
            }
        ) {
            Surface(
                shadowElevation = 6.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                CreateProductDialog(
                    onClose = { navController.popBackStack<CreateProductDialog>(inclusive = true) },
                    onSuccess = { productId ->
                        navController.navigate(
                            route = CreateMeasurement(productId),
                            navOptions = navOptions {
                                launchSingleTop = true

                                popUpTo<Search> {
                                    inclusive = false
                                }
                            }
                        )
                    }
                )
            }
        }
        // Slide beyond the screen on enter and exit
        composable<EditProductDialog>(
            enterTransition = {
                crossfadeIn() + slideInVertically(
                    animationSpec = tween(
                        easing = LinearOutSlowInEasing
                    ),
                    initialOffsetY = { it }
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    ),
                    targetOffsetY = { it }
                ) + scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(
                        easing = FastOutLinearInEasing
                    )
                )
            }
        ) {
            val (productId) = it.toRoute<EditProductDialog>()

            Surface(
                shadowElevation = 6.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                UpdateProductDialog(
                    onClose = { navController.popBackStack<EditProductDialog>(inclusive = true) },
                    onSuccess = { navController.popBackStack<EditProductDialog>(inclusive = true) },
                    viewModel = koinViewModel(
                        parameters = { parametersOf(productId) }
                    )
                )
            }
        }
    }
}
