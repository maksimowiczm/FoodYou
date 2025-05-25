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
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.navigation.CrossFadeComposableDefaults
import com.maksimowiczm.foodyou.core.navigation.ForwardBackwardComposableDefaults
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.ui.LocalNavigationSharedTransitionScope
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.rememberSearchFoodScreenState
import com.maksimowiczm.foodyou.feature.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.meal.MealScreen
import com.maksimowiczm.foodyou.feature.meal.ui.screen.MealScreenSharedTransition
import com.maksimowiczm.foodyou.feature.measurement.CreateMeasurementScreen
import com.maksimowiczm.foodyou.feature.measurement.UpdateMeasurementScreen
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
                        navController.navigate(MeasureFood(it)) {
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
                    navController.navigate(UpdateMeasurement(it)) { launchSingleTop = true }
                }
            )
        }
        forwardBackwardComposable<MeasureFood>(
            popEnterTransition = {
                if (initialState.destination.hasRoute<UpdateRecipe>()) {
                    fadeIn(snap())
                } else {
                    ForwardBackwardComposableDefaults.popEnterTransition()
                }
            },
            exitTransition = {
                if (targetState.destination.hasRoute<UpdateRecipe>()) {
                    CrossFadeComposableDefaults.exitTransition()
                } else {
                    ForwardBackwardComposableDefaults.exitTransition()
                }
            }
        ) {
            val route = it.toRoute<MeasureFood>()

            CreateMeasurementScreen(
                foodId = route.foodId,
                mealId = mealId,
                date = date,
                onBack = {
                    navController.popBackStack<MeasureFood>(inclusive = true)
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
                onRecipeClone = {
                    navController.navigate(MeasureFood(productId = it.id)) {
                        launchSingleTop = true

                        popUpTo<MeasureFood> {
                            inclusive = true
                        }
                    }
                },
                animatedVisibilityScope = this
            )
        }
        forwardBackwardComposable<UpdateMeasurement>(
            popEnterTransition = {
                if (initialState.destination.hasRoute<UpdateRecipe>()) {
                    fadeIn(snap())
                } else {
                    ForwardBackwardComposableDefaults.popEnterTransition()
                }
            },
            exitTransition = {
                if (targetState.destination.hasRoute<UpdateRecipe>()) {
                    CrossFadeComposableDefaults.exitTransition()
                } else {
                    ForwardBackwardComposableDefaults.exitTransition()
                }
            }
        ) {
            val route = it.toRoute<UpdateMeasurement>()

            UpdateMeasurementScreen(
                measurementId = route.measurementId,
                onBack = {
                    navController.popBackStack<UpdateMeasurement>(inclusive = true)
                },
                onEditFood = { foodId ->
                    when (foodId) {
                        is FoodId.Product -> navController.navigate(UpdateProduct(foodId.id)) {
                            launchSingleTop = true
                        }

                        is FoodId.Recipe -> navController.navigate(UpdateRecipe(foodId.id)) {
                            launchSingleTop = true
                        }
                    }
                },
                onRecipeClone = { productId ->
                    navController.navigate(MeasureFood(productId = productId.id)) {
                        launchSingleTop = true

                        popUpTo<UpdateMeasurement> {
                            inclusive = true
                        }
                    }
                },
                animatedVisibilityScope = this
            )
        }
        forwardBackwardComposable<CreateProduct> {
            CreateProductScreen(
                onBack = {
                    navController.popBackStack<CreateProduct>(inclusive = true)
                },
                onCreate = { productId ->
                    navController.navigate(MeasureFood(FoodId.Product(productId))) {
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
                navController.navigate(MeasureFood(FoodId.Recipe(it))) {
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

@Serializable
private data class UpdateMeasurement(
    val productMeasurementId: Long? = null,
    val recipeMeasurementId: Long? = null
) {
    constructor(measurementId: MeasurementId) : this(
        productMeasurementId = when (measurementId) {
            is MeasurementId.Product -> measurementId.id
            is MeasurementId.Recipe -> null
        },
        recipeMeasurementId = when (measurementId) {
            is MeasurementId.Recipe -> measurementId.id
            is MeasurementId.Product -> null
        }
    )

    val measurementId: MeasurementId
        get() = when {
            productMeasurementId != null -> MeasurementId.Product(productMeasurementId)
            recipeMeasurementId != null -> MeasurementId.Recipe(recipeMeasurementId)
            else -> error("Either productMeasurementId or recipeMeasurementId must be provided")
        }

    init {
        if (productMeasurementId == null && recipeMeasurementId == null) {
            error("Either productMeasurementId or recipeMeasurementId must be provided")
        }
    }
}

@Serializable
private data class MeasureFood(val productId: Long? = null, val recipeId: Long? = null) {
    constructor(foodId: FoodId) : this(
        productId = when (foodId) {
            is FoodId.Product -> foodId.id
            is FoodId.Recipe -> null
        },
        recipeId = when (foodId) {
            is FoodId.Recipe -> foodId.id
            is FoodId.Product -> null
        }
    )

    val foodId: FoodId
        get() = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Either productId or recipeId must be provided")
        }

    init {
        if (productId == null && recipeId == null) {
            error("Either productId or recipeId must be provided")
        }
    }
}
