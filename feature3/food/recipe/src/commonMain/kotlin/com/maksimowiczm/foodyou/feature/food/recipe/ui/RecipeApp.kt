package com.maksimowiczm.foodyou.feature.food.recipe.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.isComplete
import com.maksimowiczm.foodyou.feature.food.recipe.presentation.MinimalIngredient
import com.maksimowiczm.foodyou.feature.food.shared.ui.NutrientList
import com.maksimowiczm.foodyou.feature.food.shared.ui.search.FoodSearchApp
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.measurement.MeasurementType
import com.maksimowiczm.foodyou.shared.common.domain.measurement.from
import com.maksimowiczm.foodyou.shared.common.domain.measurement.rawValue
import com.maksimowiczm.foodyou.shared.common.domain.measurement.type
import com.maksimowiczm.foodyou.shared.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.IncompleteFoodsList
import foodyou.app.generated.resources.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecipeApp(
    onBack: () -> Unit,
    onSave: (RecipeFormState) -> Unit,
    onEditFood: (FoodId) -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    state: RecipeFormState,
    topBarTitle: String,
    mainRecipeId: FoodId.Recipe?,
    recipe: Recipe?,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = Form, modifier = modifier) {
        forwardBackwardComposable<Form> {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(topBarTitle) },
                        navigationIcon = { ArrowBackIconButton(onBack) },
                        actions = {
                            FilledIconButton(onClick = { onSave(state) }, enabled = state.isValid) {
                                Icon(
                                    imageVector = Icons.Outlined.Save,
                                    contentDescription = stringResource(Res.string.action_save),
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize()
                            .imePadding()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = paddingValues,
                ) {
                    item {
                        RecipeForm(
                            state = state,
                            onAddIngredient = {
                                navController.navigate(IngredientsSearch) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onEditIngredient = {
                                navController.navigate(EditIngredient(it)) {
                                    launchSingleTop = true
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp),
                        )
                    }

                    if (recipe != null) {
                        item {
                            HorizontalDivider(Modifier.padding(bottom = 8.dp))
                            Text(
                                text = stringResource(Res.string.headline_summary),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                            )

                            val facts =
                                recipe.nutritionFacts *
                                    Measurement.Package(1.0).weight(recipe.totalWeight) / 100.0
                            NutrientList(
                                facts = facts,
                                modifier = Modifier.padding(horizontal = 8.dp),
                            )

                            if (!facts.isComplete) {
                                val foods =
                                    recipe
                                        .allIngredients()
                                        .filter { it is Product }
                                        .filter { !it.nutritionFacts.isComplete }

                                IncompleteFoodsList(
                                    foods = foods.map { it.headline }.distinct(),
                                    modifier =
                                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    onFoodClick = { i -> onEditFood(foods[i].id) },
                                )
                            }
                        }
                    }
                }
            }
        }
        forwardBackwardComposable<IngredientsSearch> {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(Res.string.headline_choose_ingredient)) },
                        navigationIcon = {
                            ArrowBackIconButton(
                                onClick = { navController.popBackStack<IngredientsSearch>(true) }
                            )
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
                contentWindowInsets =
                    ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top),
            ) { paddingValues ->
                FoodSearchApp(
                    onFoodClick = { model, measurement ->
                        navController.navigate(IngredientMeasurement(model.id, measurement)) {
                            launchSingleTop = true
                        }
                    },
                    onUpdateUsdaApiKey = onUpdateUsdaApiKey,
                    excludedRecipe = mainRecipeId,
                    modifier =
                        Modifier.fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .padding(paddingValues)
                            .consumeWindowInsets(paddingValues),
                )
            }
        }
        forwardBackwardComposable<IngredientMeasurement> {
            val route = it.toRoute<IngredientMeasurement>()

            MeasureIngredientScreen(
                onBack = { navController.popBackStack<IngredientMeasurement>(true) },
                onSave = { measurement ->
                    state.addIngredient(
                        MinimalIngredient(foodId = route.foodId, measurement = measurement)
                    )
                    navController.popBackStack<IngredientMeasurement>(true)
                    navController.popBackStack<IngredientsSearch>(true, saveState = true)
                },
                measurement = route.measurement,
                viewModel = koinViewModel { parametersOf(route.foodId) },
            )
        }
        forwardBackwardComposable<EditIngredient> {
            val route = it.toRoute<EditIngredient>()

            val ingredient = state.ingredients.getOrNull(route.index)

            LaunchedEffect(ingredient) {
                if (ingredient == null) {
                    navController.popBackStack<EditIngredient>(true)
                }
            }

            if (ingredient != null) {
                MeasureIngredientScreen(
                    onBack = { navController.popBackStack<EditIngredient>(true) },
                    onSave = { measurement ->
                        state.updateIngredient(
                            route.index,
                            MinimalIngredient(foodId = ingredient.foodId, measurement = measurement),
                        )
                        navController.popBackStack<EditIngredient>(true)
                    },
                    measurement = ingredient.measurement,
                    viewModel = koinViewModel { parametersOf(ingredient.foodId) },
                )
            }
        }
    }
}

@Serializable private data object Form

@Serializable private data object IngredientsSearch

@Serializable private data class EditIngredient(val index: Int)

@Serializable
private data class IngredientMeasurement(
    val productId: Long?,
    val recipeId: Long?,
    val measurementType: MeasurementType,
    val quantity: Double,
) {
    constructor(
        foodId: FoodId,
        measurement: Measurement,
    ) : this(
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id,
        measurementType = measurement.type,
        quantity = measurement.rawValue,
    )

    val foodId: FoodId
        get() =
            when {
                productId != null -> FoodId.Product(productId)
                recipeId != null -> FoodId.Recipe(recipeId)
                else -> throw IllegalStateException("Food ID is not set")
            }

    val measurement: Measurement
        get() = Measurement.from(measurementType, quantity)
}
