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
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.CrossFadeComposableDefaults
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.ui.LocalNavigationSharedTransitionScope
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.rememberSearchFoodScreenState
import com.maksimowiczm.foodyou.feature.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.meal.MealScreen
import com.maksimowiczm.foodyou.feature.meal.ui.screen.MealScreenSharedTransition
import com.maksimowiczm.foodyou.feature.measurement.CreateMeasurement
import com.maksimowiczm.foodyou.feature.measurement.UpdateMeasurement
import com.maksimowiczm.foodyou.feature.measurement.measurementGraph
import com.maksimowiczm.foodyou.feature.product.CreateProductScreen
import com.maksimowiczm.foodyou.feature.product.UpdateProductScreen
import com.maksimowiczm.foodyou.feature.recipe.CreateRecipe
import com.maksimowiczm.foodyou.feature.recipe.UpdateRecipe
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
                if (initialState.destination.hasRoute<CreateRecipe>()
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
                    onFoodClick = {
                        navController.navigate(
                            CreateMeasurement(
                                foodId = it,
                                mealId = mealId,
                                epochDay = epochDay
                            )
                        ) {
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
                                key = MealScreenSharedTransition.FAB_CONTAINER
                            ),
                            enter = MealScreenSharedTransition.screenContainerEnterTransition,
                            exit = MealScreenSharedTransition.screenContainerExitTransition,
                            animatedVisibilityScope = this@crossfadeComposable
                        )
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = MealScreenSharedTransition.FAB_CONTENT
                            ),
                            animatedVisibilityScope = this@crossfadeComposable,
                            enter = MealScreenSharedTransition.screenContentEnterTransition,
                            exit = MealScreenSharedTransition.screenContentExitTransition
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
            val homeSTS = LocalNavigationSharedTransitionScope.current
                ?: error("No home shared transition scope")
            val addFoodSTS = LocalAddFoodSharedTransitionScope.current
                ?: error("No add food shared transition scope")

            MealScreen(
                screenSts = addFoodSTS,
                screenScope = this,
                enterSts = homeSTS,
                enterScope = outerAnimatedScope,
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
                onEditMeasurement = {
                    navController.navigate(UpdateMeasurement(it)) {
                        launchSingleTop = true

                        popUpTo<CreateProduct> {
                            inclusive = true
                        }
                    }
                }
            )
        }
        forwardBackwardComposable<CreateProduct> {
            CreateProductScreen(
                onBack = {
                    navController.popBackStack<CreateProduct>(inclusive = true)
                },
                onCreate = { productId ->
                    navController.navigate(
                        CreateMeasurement(
                            foodId = FoodId.Product(productId),
                            mealId = mealId,
                            epochDay = epochDay
                        )
                    ) {
                        launchSingleTop = true

                        popUpTo<CreateProduct> {
                            inclusive = true
                        }
                    }
                }
            )
        }
        forwardBackwardComposable<UpdateProduct> {
            val (productId) = it.toRoute<UpdateProduct>()

            UpdateProductScreen(
                productId = productId,
                onBack = {
                    navController.popBackStack<UpdateProduct>(inclusive = true)
                },
                onUpdate = {
                    navController.popBackStack<UpdateProduct>(inclusive = true)
                }
            )
        }
        recipeGraph(
            onCreateClose = {
                navController.popBackStack<CreateRecipe>(inclusive = true)
            },
            onCreate = {
                navController.navigate(
                    CreateMeasurement(
                        foodId = FoodId.Recipe(it),
                        mealId = mealId,
                        epochDay = epochDay
                    )
                ) {
                    launchSingleTop = true

                    popUpTo<CreateRecipe> {
                        inclusive = true
                    }
                }
            },
            onUpdateClose = {
                navController.popBackStack<UpdateRecipe>(inclusive = true)
            },
            onUpdate = {
                navController.popBackStack<UpdateRecipe>(inclusive = true)
            }
        )
        measurementGraph(
            createMeasurementOnBack = {
                navController.popBackStack<CreateMeasurement>(inclusive = true)
            },
            updateMeasurementOnBack = {
                navController.popBackStack<UpdateMeasurement>(inclusive = true)
            },
            onEditFood = {
                when (it) {
                    is FoodId.Product -> navController.navigate(UpdateProduct(it.id)) {
                        launchSingleTop = true
                    }

                    is FoodId.Recipe -> navController.navigate(UpdateRecipe(it.id)) {
                        launchSingleTop = true
                    }
                }
            },
            // Okay this is a bit akward
            createMeasurementOnRecipeClone = {
                navController.navigate(
                    CreateMeasurement(
                        foodId = it,
                        mealId = mealId,
                        epochDay = epochDay
                    )
                ) {
                    launchSingleTop = true

                    popUpTo<CreateMeasurement> {
                        inclusive = true
                    }
                }
            },
            updateMeasurementOnRecipeClone = {
                navController.navigate(
                    CreateMeasurement(
                        foodId = it,
                        mealId = mealId,
                        epochDay = epochDay
                    )
                ) {
                    launchSingleTop = true

                    popUpTo<UpdateMeasurement> {
                        inclusive = true
                    }
                }
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
private data object CreateProduct

@Serializable
private data class UpdateProduct(val productId: Long)
