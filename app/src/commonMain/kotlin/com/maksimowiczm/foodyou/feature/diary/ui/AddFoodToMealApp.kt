package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.AddFoodSearchViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose.AddFoodSearch
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose.AddFoodSearchScreen
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose.rememberAddFoodSearchState
import com.maksimowiczm.foodyou.feature.diary.ui.meal.compose.DiaryDayMealScreen
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.CreateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.MeasurementScreen
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.UpdateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.product.create.CreateProductDialog
import com.maksimowiczm.foodyou.feature.diary.ui.product.update.UpdateProductDialog
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.ui.motion.crossfadeIn
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AddFoodToMealApp(
    outerScope: AnimatedVisibilityScope,
    outerOnBack: () -> Unit,
    mealId: Long,
    epochDay: Int,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    skipToSearchScreen: Boolean = false
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalMealSharedTransitionScope provides this
        ) {
            AppNavHost(
                outerScope = outerScope,
                outerOnBack = outerOnBack,
                mealId = mealId,
                epochDay = epochDay,
                onGoToOpenFoodFactsSettings = onGoToSettings,
                modifier = modifier,
                navController = navController,
                skipToSearchScreen = skipToSearchScreen
            )
        }
    }
}

@Serializable
private data object MealHome

@Serializable
private data class Search(val startOnBarcodeScanner: Boolean)

@Serializable
private data class CreateMeasurement(val productId: Long)

@Serializable
private data class EditMeasurement(val measurementId: Long)

@Serializable
private data object CreateProductDialog

@Serializable
private data class EditProductDialog(val productId: Long)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AppNavHost(
    outerScope: AnimatedVisibilityScope,
    outerOnBack: () -> Unit,
    mealId: Long,
    epochDay: Int,
    onGoToOpenFoodFactsSettings: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    skipToSearchScreen: Boolean = false
) {
    val date = LocalDate.fromEpochDays(epochDay)

    // Scope search state to whole app
    val searchState = rememberAddFoodSearchState()
    val searchViewModel = koinViewModel<AddFoodSearchViewModel>(
        parameters = { parametersOf(mealId, date) }
    )

    val startDestination: Any = if (skipToSearchScreen) {
        Search(
            startOnBarcodeScanner = false
        )
    } else {
        MealHome
    }

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
                        route = Search(
                            startOnBarcodeScanner = false
                        ),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onBarcodeScan = {
                    navController.navigate(
                        route = Search(
                            startOnBarcodeScanner = true
                        ),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onEditEntry = {
                    when (it) {
                        is MeasurementId.Product -> navController.navigate(
                            route = EditMeasurement(it.measurementId),
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    }
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
            val (startOnBarcodeScanner) = it.toRoute<Search>()

            val sharedTransitionScope =
                LocalMealSharedTransitionScope.current ?: error("No shared transition scope found")

            with(sharedTransitionScope) {
                AddFoodSearch(
                    mealId = mealId,
                    date = date,
                    onBack = {
                        // If stack is empty call outer on back otherwise pop search
                        if (navController.currentBackStack.value.size == 2) {
                            outerOnBack()
                        } else {
                            navController.popBackStack()
                        }
                    },
                    onProductClick = {
                        navController.navigate(
                            route = CreateMeasurement(it),
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    },
                    onCreateProduct = {
                        navController.navigate(
                            route = CreateProductDialog,
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    },
                    onGoToOpenFoodFactsSettings = onGoToOpenFoodFactsSettings,
                    initialScreen = if (startOnBarcodeScanner) {
                        AddFoodSearchScreen.BarcodeScanner
                    } else {
                        AddFoodSearchScreen.List
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
                    state = searchState
                )
            }
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
