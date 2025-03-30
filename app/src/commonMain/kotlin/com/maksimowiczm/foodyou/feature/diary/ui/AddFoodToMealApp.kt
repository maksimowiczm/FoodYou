package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.CreateFoodProductMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.EditFoodProductMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.measurementGraph
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.AddFoodSearchViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose.AddFoodSearch
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose.AddFoodSearchScreen
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.compose.rememberAddFoodSearchState
import com.maksimowiczm.foodyou.feature.diary.ui.meal.compose.DiaryDayMealScreen
import com.maksimowiczm.foodyou.feature.diary.ui.product.CreateProduct
import com.maksimowiczm.foodyou.feature.diary.ui.product.EditProduct
import com.maksimowiczm.foodyou.feature.diary.ui.product.productGraph
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
                            route = EditFoodProductMeasurement(it.measurementId),
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
                if (initialState.destination.hasRoute<CreateProduct>()) {
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
                            route = CreateFoodProductMeasurement(it),
                            navOptions = navOptions {
                                launchSingleTop = true
                            }
                        )
                    },
                    onCreateProduct = {
                        navController.navigate(
                            route = CreateProduct,
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

        measurementGraph(
            mealId = mealId,
            date = date,
            onCreateBack = {
                navController.popBackStack<CreateFoodProductMeasurement>(inclusive = true)
            },
            onEditBack = {
                navController.popBackStack<EditFoodProductMeasurement>(inclusive = true)
            },
            onEditProduct = { productId ->
                navController.navigate(
                    route = EditProduct(productId),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        )

        productGraph(
            onCreateClose = { navController.popBackStack<CreateProduct>(inclusive = true) },
            onCreateSuccess = { productId ->
                navController.navigate(
                    route = CreateFoodProductMeasurement(productId),
                    navOptions = navOptions {
                        launchSingleTop = true

                        popUpTo<Search> {
                            inclusive = false
                        }
                    }
                )
            },
            onEditClose = { navController.popBackStack<EditProduct>(inclusive = true) },
            onEditSuccess = { navController.popBackStack<EditProduct>(inclusive = true) }
        )
    }
}
