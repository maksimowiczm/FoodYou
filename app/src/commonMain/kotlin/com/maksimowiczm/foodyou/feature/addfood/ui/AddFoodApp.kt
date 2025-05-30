package com.maksimowiczm.foodyou.feature.addfood.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodScreen
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.rememberSearchFoodScreenState
import com.maksimowiczm.foodyou.feature.measurement.CreateMeasurementScreen
import com.maksimowiczm.foodyou.feature.product.CreateProduct
import com.maksimowiczm.foodyou.feature.product.UpdateProduct
import com.maksimowiczm.foodyou.feature.product.productGraph
import com.maksimowiczm.foodyou.feature.recipe.CreateRecipe
import com.maksimowiczm.foodyou.feature.recipe.UpdateRecipe
import com.maksimowiczm.foodyou.feature.recipe.recipeGraph
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun AddFoodApp(
    onBack: () -> Unit,
    mealId: Long,
    epochDay: Int,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val date = LocalDate.fromEpochDays(epochDay)
    val searchViewModel = koinViewModel<SearchFoodViewModel>(
        parameters = { parametersOf(mealId, date) }
    )
    val searchScreenState = rememberSearchFoodScreenState()

    NavHost(
        navController = navController,
        startDestination = SearchFood,
        modifier = modifier
    ) {
        crossfadeComposable<SearchFood> {
            SearchFoodScreen(
                onBack = onBack,
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
                viewModel = searchViewModel,
                state = searchScreenState
            )
        }
        forwardBackwardComposable<MeasureFood> {
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
                onRecipeClone = { it, _, _ ->
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
        productGraph(
            createOnBack = {
                navController.popBackStack<CreateProduct>(inclusive = true)
            },
            updateOnBack = {
                navController.popBackStack<UpdateProduct>(inclusive = true)
            },
            onCreateProduct = { foodId ->
                navController.navigate(MeasureFood(foodId)) {
                    launchSingleTop = true

                    popUpTo<CreateProduct> {
                        inclusive = true
                    }
                }
            },
            onUpdateProduct = { foodId ->
                navController.navigate(MeasureFood(foodId)) {
                    launchSingleTop = true

                    popUpTo<UpdateProduct> {
                        inclusive = true
                    }
                }
            }
        )
        recipeGraph(
            createOnBack = {
                navController.popBackStack<CreateRecipe>(inclusive = true)
            }
//            onCreate = {
//                navController.navigate(MeasureFood(FoodId.Recipe(it))) {
//                    launchSingleTop = true
//
//                    popUpTo<CreateRecipe> {
//                        inclusive = true
//                    }
//                }
//            },
//            onUpdateClose = {
//                navController.popBackStack<UpdateRecipe>(inclusive = true)
//            },
//            onUpdate = {
//                navController.popBackStack<UpdateRecipe>(inclusive = true)
//            }
        )
    }
}

@Serializable
private data object SearchFood

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
        require(productId != null || recipeId != null) {
            "Either productId or recipeId must be provided"
        }
    }
}
