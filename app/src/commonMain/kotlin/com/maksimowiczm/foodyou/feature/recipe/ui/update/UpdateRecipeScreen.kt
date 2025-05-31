package com.maksimowiczm.foodyou.feature.recipe.ui.update

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.recipe.ui.FormContent
import com.maksimowiczm.foodyou.feature.recipe.ui.MinimalIngredient
import com.maksimowiczm.foodyou.feature.recipe.ui.measure.MeasureIngredientScreen
import com.maksimowiczm.foodyou.feature.recipe.ui.rememberRecipeFormState
import com.maksimowiczm.foodyou.feature.recipe.ui.search.IngredientsSearchScreen
import com.maksimowiczm.foodyou.feature.recipe.ui.toMinimalIngredient
import foodyou.app.generated.resources.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UpdateRecipeScreen(
    recipeId: FoodId.Recipe,
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<UpdateRecipeViewModel>(
        parameters = { parametersOf(recipeId) }
    )

    val latestOnUpdate by rememberUpdatedState(onUpdate)
    LaunchedCollectWithLifecycle(viewModel.eventBus) {
        when (it) {
            is UpdateRecipeEvent.RecipeUpdated -> latestOnUpdate()
        }
    }

    val recipe = viewModel.recipe.collectAsStateWithLifecycle().value

    if (recipe == null) {
        // TODO loading state
        Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        return
    }

    val navController = rememberNavController()

    var ingredientsState = rememberSaveable(
        stateSaver = MinimalIngredient.Companion.ListSaver
    ) {
        mutableStateOf<List<MinimalIngredient>>(recipe.ingredients.map { it.toMinimalIngredient() })
    }

    val formState = rememberRecipeFormState(
        initialName = recipe.name,
        initialServings = recipe.servings
    )
    val ingredients = viewModel
        .observeIngredients(ingredientsState.value)
        .collectAsStateWithLifecycle(emptyList()).value

    NavHost(
        navController = navController,
        startDestination = Form,
        modifier = modifier
    ) {
        forwardBackwardComposable<Form> {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

            FormContent(
                ingredients = ingredients,
                formState = formState,
                onAddIngredient = {
                    navController.navigate(Search) {
                        launchSingleTop = true
                    }
                },
                onEditIngredient = { ingredient ->
                    val index = ingredients.indexOfFirst { it.food.id == ingredient.food.id }
                    if (index != -1) {
                        navController.navigate(UpdateMeasurement(index)) {
                            launchSingleTop = true
                        }
                    }
                },
                onRemoveIngredient = { ingredient ->
                    ingredientsState.value = ingredientsState.value.toMutableList().apply {
                        removeAt(ingredient)
                    }
                },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(stringResource(Res.string.headline_update_recipe))
                        },
                        navigationIcon = {
                            ArrowBackIconButton(onBack)
                        },
                        actions = {
                            FilledIconButton(
                                onClick = {
                                    viewModel.onSave(
                                        name = formState.nameState.value,
                                        servings = formState.servingsState.value,
                                        ingredients = ingredients.toList()
                                    )
                                },
                                enabled = formState.isValid && ingredients.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Save,
                                    contentDescription = stringResource(Res.string.action_save)
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        }
        forwardBackwardComposable<Search> {
            IngredientsSearchScreen(
                onBack = {
                    navController.popBackStack(Search, inclusive = true)
                },
                onIngredient = {
                    val route = Measure(it.food.id)

                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        forwardBackwardComposable<Measure> {
            val route = it.toRoute<Measure>()
            val foodId = route.foodId

            MeasureIngredientScreen(
                foodId = route.foodId,
                selected = null,
                onBack = {
                    navController.popBackStack<Measure>(inclusive = true)
                },
                onMeasurement = { measurement ->
                    ingredientsState.value = ingredientsState.value + MinimalIngredient(
                        foodId = foodId,
                        measurement = measurement
                    )
                    navController.popBackStack<Search>(
                        inclusive = true
                    )
                }
            )
        }
        forwardBackwardComposable<UpdateMeasurement> {
            val route = it.toRoute<UpdateMeasurement>()
            val index = route.index
            val ingredient = ingredients.getOrNull(index)

            if (ingredient == null) {
                // Handle the case where the ingredient is not found
                navController.popBackStack<Search>(inclusive = true)
                return@forwardBackwardComposable
            }

            MeasureIngredientScreen(
                foodId = ingredient.food.id,
                selected = ingredient.measurement,
                onBack = {
                    navController.popBackStack<UpdateMeasurement>(inclusive = true)
                },
                onMeasurement = { measurement ->
                    ingredientsState.value = ingredientsState.value.toMutableList().apply {
                        set(index, this[index].copy(measurement = measurement))
                    }
                    navController.popBackStack<UpdateMeasurement>(inclusive = true)
                }
            )
        }
    }
}

@Serializable
private data object Form

@Serializable
private data object Search

@Serializable
private data class Measure(val productId: Long?, val recipeId: Long?) {
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
private data class UpdateMeasurement(val index: Int)
