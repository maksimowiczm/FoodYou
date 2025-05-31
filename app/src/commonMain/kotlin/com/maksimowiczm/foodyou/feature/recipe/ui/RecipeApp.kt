package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.recipe.domain.Ingredient
import com.maksimowiczm.foodyou.feature.recipe.ui.measure.MeasureIngredientScreen
import com.maksimowiczm.foodyou.feature.recipe.ui.search.IngredientsSearchScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecipeApp(
    recipeId: FoodId.Recipe?,
    titleRes: StringResource,
    ingredients: List<MinimalIngredient>,
    observedIngredients: (List<MinimalIngredient>) -> Flow<List<Ingredient>>,
    onSave: (name: String, servings: Int, ingredients: List<Ingredient>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    formState: RecipeFormState = rememberRecipeFormState()
) {
    val navController = rememberNavController()

    var ingredientsState = rememberSaveable(
        stateSaver = MinimalIngredient.ListSaver
    ) {
        mutableStateOf(ingredients)
    }

    val observedIngredients =
        observedIngredients(ingredientsState.value)
            .collectAsStateWithLifecycle(emptyList()).value

    NavHost(
        navController = navController,
        startDestination = RecipeForm,
        modifier = modifier
    ) {
        forwardBackwardComposable<RecipeForm> {
            FormContent(
                titleRes = titleRes,
                ingredients = observedIngredients,
                formState = formState,
                onSave = onSave,
                onBack = onBack,
                onAddIngredient = {
                    navController.navigate(IngredientsSearch) {
                        launchSingleTop = true
                    }
                },
                onEditIngredient = { ingredient ->
                    val index =
                        observedIngredients.indexOfFirst { it.food.id == ingredient.food.id }
                    if (index != -1) {
                        navController.navigate(UpdateIngredientMeasurement(index)) {
                            launchSingleTop = true
                        }
                    }
                },
                onRemoveIngredient = { ingredient ->
                    ingredientsState.value = ingredientsState.value.toMutableList().apply {
                        removeAt(ingredient)
                    }
                }
            )
        }

        forwardBackwardComposable<IngredientsSearch> {
            IngredientsSearchScreen(
                recipeId = recipeId,
                onBack = {
                    navController.popBackStack(IngredientsSearch, inclusive = true)
                },
                onIngredient = {
                    val route = MeasureIngredient(it.food.id)
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        forwardBackwardComposable<MeasureIngredient> {
            val route = it.toRoute<MeasureIngredient>()
            val foodId = route.foodId

            MeasureIngredientScreen(
                foodId = route.foodId,
                selected = null,
                onBack = {
                    navController.popBackStack<MeasureIngredient>(inclusive = true)
                },
                onMeasurement = { measurement ->
                    ingredientsState.value = ingredientsState.value + MinimalIngredient(
                        foodId = foodId,
                        measurement = measurement
                    )
                    navController.popBackStack<IngredientsSearch>(inclusive = true)
                }
            )
        }

        forwardBackwardComposable<UpdateIngredientMeasurement> {
            val route = it.toRoute<UpdateIngredientMeasurement>()
            val index = route.index
            val ingredient = observedIngredients.getOrNull(index)

            if (ingredient == null) {
                navController.popBackStack<IngredientsSearch>(inclusive = true)
                return@forwardBackwardComposable
            }

            MeasureIngredientScreen(
                foodId = ingredient.food.id,
                selected = ingredient.measurement,
                onBack = {
                    navController.popBackStack<UpdateIngredientMeasurement>(inclusive = true)
                },
                onMeasurement = { measurement ->
                    ingredientsState.value = ingredientsState.value.toMutableList().apply {
                        set(index, this[index].copy(measurement = measurement))
                    }
                    navController.popBackStack<UpdateIngredientMeasurement>(inclusive = true)
                }
            )
        }
    }
}

@Serializable
private data object RecipeForm

@Serializable
private data object IngredientsSearch

@Serializable
private data class MeasureIngredient(val productId: Long?, val recipeId: Long?) {
    constructor(foodId: FoodId) : this(
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id
    )

    init {
        require(productId != null || recipeId != null) {
            "Either productId or recipeId must be provided"
        }
    }

    val foodId
        get() = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Either productId or recipeId must be provided")
        }
}

@Serializable
private data class UpdateIngredientMeasurement(val index: Int)
