package com.maksimowiczm.foodyou.feature.food.ui.recipe

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
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.Recipe
import com.maksimowiczm.foodyou.feature.food.ui.FoodSearchApp
import com.maksimowiczm.foodyou.feature.food.ui.NutrientList
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.from
import com.maksimowiczm.foodyou.feature.measurement.domain.rawValue
import com.maksimowiczm.foodyou.feature.measurement.domain.type
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
    state: RecipeFormState,
    topBarTitle: String,
    mainRecipeId: FoodId.Recipe?,
    recipe: Recipe?,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Form,
        modifier = modifier
    ) {
        forwardBackwardComposable<Form> {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(topBarTitle)
                        },
                        navigationIcon = {
                            ArrowBackIconButton(onBack)
                        },
                        actions = {
                            FilledIconButton(
                                onClick = { onSave(state) },
                                enabled = state.isValid
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Save,
                                    contentDescription = stringResource(Res.string.action_save)
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = paddingValues
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
                            contentPadding = PaddingValues(
                                horizontal = 16.dp
                            )
                        )
                    }

                    if (recipe != null) {
                        item {
                            HorizontalDivider(Modifier.padding(bottom = 8.dp))
                            Text(
                                text = stringResource(Res.string.headline_summary),
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )

                            val facts = recipe.nutritionFacts * Measurement.Package(1f)
                                .weight(recipe.totalWeight) / 100f
                            NutrientList(
                                facts = facts,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp
                                )
                            )
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
                        title = {
                            Text(stringResource(Res.string.headline_choose_ingredient))
                        },
                        navigationIcon = {
                            ArrowBackIconButton(
                                onClick = {
                                    navController.popBackStack<IngredientsSearch>(true)
                                }
                            )
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(
                    WindowInsetsSides.Top
                )
            ) { paddingValues ->
                FoodSearchApp(
                    onFoodClick = { food, measurement ->
                        navController.navigate(IngredientMeasurement(food.id, measurement)) {
                            launchSingleTop = true
                        }
                    },
                    excludedRecipe = mainRecipeId,
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                )
            }
        }
        forwardBackwardComposable<IngredientMeasurement> {
            val route = it.toRoute<IngredientMeasurement>()

            MeasureIngredientScreen(
                onBack = {
                    navController.popBackStack<IngredientMeasurement>(true)
                },
                onSave = { measurement ->
                    state.addIngredient(
                        MinimalIngredient(
                            foodId = route.foodId,
                            measurement = measurement
                        )
                    )
                    navController.popBackStack<IngredientMeasurement>(true)
                    navController.popBackStack<IngredientsSearch>(true, saveState = true)
                },
                measurement = route.measurement,
                viewModel = koinViewModel {
                    parametersOf(route.foodId)
                }
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
                    onBack = {
                        navController.popBackStack<IngredientMeasurement>(true)
                    },
                    onSave = { measurement ->
                        state.updateIngredient(
                            route.index,
                            MinimalIngredient(
                                foodId = ingredient.foodId,
                                measurement = measurement
                            )
                        )
                        navController.popBackStack<EditIngredient>(true)
                    },
                    measurement = ingredient.measurement,
                    viewModel = koinViewModel {
                        parametersOf(ingredient.foodId)
                    }
                )
            }
        }
    }
}

@Serializable
private data object Form

@Serializable
private data object IngredientsSearch

@Serializable
private data class EditIngredient(val index: Int)

@Serializable
private data class IngredientMeasurement(
    val productId: Long?,
    val recipeId: Long?,
    val measurementType: MeasurementType,
    val quantity: Float
) {
    constructor(
        foodId: FoodId,
        measurement: Measurement
    ) : this(
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id,
        measurementType = measurement.type,
        quantity = measurement.rawValue
    )

    val foodId: FoodId
        get() = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> throw IllegalStateException("Food ID is not set")
        }

    val measurement: Measurement
        get() = Measurement.from(measurementType, quantity)
}
