package com.maksimowiczm.foodyou.feature.addfood.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.MeasurementId
import com.maksimowiczm.foodyou.core.navigation.CrossFadeComposableDefaults
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.feature.addfood.SearchSharedTransition
import com.maksimowiczm.foodyou.feature.addfood.ui.measurement.CreateMeasurementScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.measurement.UpdateMeasurementScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.rememberSearchFoodScreenState
import com.maksimowiczm.foodyou.feature.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.meal.MealScreen
import com.maksimowiczm.foodyou.feature.product.CreateProduct
import com.maksimowiczm.foodyou.feature.product.UpdateProduct
import com.maksimowiczm.foodyou.feature.product.productGraph
import com.maksimowiczm.foodyou.feature.recipe.CreateRecipe
import com.maksimowiczm.foodyou.feature.recipe.recipeGraph
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

// I had to create separate shared transition scope for AddFood
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun AddFoodApp(
    outerOnBack: () -> Unit,
    outerAnimatedScope: AnimatedContentScope,
    onOpenFoodFactsSettings: () -> Unit,
    mealId: Long,
    epochDay: Int,
    skipToSearch: Boolean,
    modifier: Modifier = Modifier
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalAddFoodSharedTransitionScope provides this
        ) {
            AddFoodNavHost(
                outerOnBack = outerOnBack,
                outerAnimatedScope = outerAnimatedScope,
                onOpenFoodFactsSettings = onOpenFoodFactsSettings,
                mealId = mealId,
                epochDay = epochDay,
                skipToSearch = skipToSearch,
                modifier = modifier
            )
        }
    }
}

// I had to scope search view model and list state to whole NavHost
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun AddFoodNavHost(
    outerOnBack: () -> Unit,
    outerAnimatedScope: AnimatedContentScope,
    onOpenFoodFactsSettings: () -> Unit,
    mealId: Long,
    epochDay: Int,
    skipToSearch: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val date = LocalDate.fromEpochDays(epochDay)
    val searchViewModel = koinViewModel<SearchFoodViewModel>(
        parameters = { parametersOf(mealId, date) }
    )
    val searchScreenState = rememberSearchFoodScreenState()

    NavHost(
        navController = navController,
        startDestination = if (skipToSearch) SearchFood else Meal,
        modifier = modifier
    ) {
        crossfadeComposable<SearchFood>(
            popEnterTransition = {
                if (initialState.destination.hasRoute<CreateProduct>() ||
                    initialState.destination.hasRoute<CreateRecipe>()
                ) {
                    fadeIn(snap())
                } else {
                    CrossFadeComposableDefaults.enterTransition()
                }
            }
        ) {
            val sts = LocalAddFoodSharedTransitionScope.current
                ?: error("No add food shared transition scope")

            with(sts) {
                SearchFoodScreen(
                    onBack = {
                        // If stack is empty call outer on back otherwise pop search
                        if (navController.currentBackStack.value.size == 2) {
                            outerOnBack()
                        } else {
                            navController.popBackStack<SearchFood>(inclusive = true)
                        }
                    },
                    onProductAdd = {
                        navController.navigate(CreateProduct) {
                            launchSingleTop = true
                        }
                    },
                    onRecipeAdd = {
                        navController.navigate(CreateRecipe) {
                            launchSingleTop = true
                        }
                    },
                    onOpenFoodFactsSettings = onOpenFoodFactsSettings,
                    onFoodClick = {
                        val route = when (it) {
                            is FoodId.Product -> MeasureProduct(it.id)
                            is FoodId.Recipe -> TODO()
                        }

                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    },
                    onBarcodeScanner = {
                        navController.navigate(SearchFoodBarcodeScanner) {
                            launchSingleTop = true
                        }
                    },
                    viewModel = searchViewModel,
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
                    state = searchScreenState
                )
            }
        }
        crossfadeComposable<SearchFoodBarcodeScanner> {
            CameraBarcodeScannerScreen(
                onClose = {
                    navController.navigate(SearchFood) {
                        popUpTo<SearchFoodBarcodeScanner> {
                            inclusive = true
                        }
                        popUpTo<SearchFood> {
                            inclusive = true
                        }
                    }
                },
                onBarcodeScan = {
                    searchViewModel.onSearch(it)

                    navController.navigate(SearchFood) {
                        popUpTo<SearchFoodBarcodeScanner> {
                            inclusive = true
                        }
                        popUpTo<SearchFood> {
                            inclusive = true
                        }
                    }
                }
            )
        }
        crossfadeComposable<Meal> {
            MealScreen(
                navigationScope = this,
                mealHeaderScope = outerAnimatedScope,
                mealId = mealId,
                date = date,
                onAddFood = {
                    navController.navigate(SearchFood) {
                        launchSingleTop = true
                    }
                },
                onBarcodeScanner = {
                    navController.navigate(SearchFood) {
                        launchSingleTop = true
                    }
                    navController.navigate(SearchFoodBarcodeScanner) {
                        launchSingleTop = true
                    }
                },
                onEditEntry = {
                    when (it) {
                        is MeasurementId.Product ->
                            navController.navigate(UpdateProductMeasurement(it.id)) {
                                launchSingleTop = true
                            }
                    }
                }
            )
        }
        crossfadeComposable<MeasureProduct>(
            popEnterTransition = {
                if (initialState.destination.hasRoute<UpdateProduct>()) {
                    fadeIn(snap())
                } else {
                    CrossFadeComposableDefaults.enterTransition()
                }
            }
        ) {
            val (productId) = it.toRoute<MeasureProduct>()

            val foodId = FoodId.Product(productId)

            CreateMeasurementScreen(
                mealId = mealId,
                date = date,
                foodId = foodId,
                onBack = {
                    navController.popBackStack<MeasureProduct>(inclusive = true)
                },
                onDelete = {
                    navController.popBackStack<MeasureProduct>(inclusive = true)
                },
                onEdit = {
                    navController.navigate(UpdateProduct(productId)) {
                        launchSingleTop = true
                    }
                }
            )
        }
        crossfadeComposable<UpdateProductMeasurement>(
            popEnterTransition = {
                if (initialState.destination.hasRoute<UpdateProduct>()) {
                    fadeIn(snap())
                } else {
                    CrossFadeComposableDefaults.enterTransition()
                }
            }
        ) {
            val (id) = it.toRoute<UpdateProductMeasurement>()

            val measurementId = MeasurementId.Product(id)

            UpdateMeasurementScreen(
                measurementId = measurementId,
                onBack = {
                    navController.popBackStack<UpdateProductMeasurement>(inclusive = true)
                },
                onDelete = {
                    navController.popBackStack<UpdateProductMeasurement>(inclusive = true)
                },
                onEdit = {
                    navController.navigate(UpdateProduct(id)) {
                        launchSingleTop = true
                    }
                }
            )
        }
        productGraph(
            onCreateProduct = {
                navController.navigate(MeasureProduct(it)) {
                    launchSingleTop = true

                    popUpTo<SearchFood> {
                        inclusive = false
                    }
                }
            },
            onCreateClose = {
                navController.popBackStack<CreateProduct>(inclusive = true)
            },
            onUpdateProduct = {
                navController.popBackStack<UpdateProduct>(inclusive = true)
            },
            onUpdateClose = {
                navController.popBackStack<UpdateProduct>(inclusive = true)
            }
        )
        recipeGraph(
            onCreateClose = {
                navController.popBackStack<CreateRecipe>(inclusive = true)
            },
            onCreate = {
                // TODO
            }
        )
    }
}

@Serializable
private data object SearchFood

@Serializable
private data object SearchFoodBarcodeScanner

@Serializable
private data object Meal

@Serializable
private data class MeasureProduct(val productId: Long)

@Serializable
private data class UpdateProductMeasurement(val measurementId: Long)
