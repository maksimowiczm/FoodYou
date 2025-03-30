package com.maksimowiczm.foodyou.feature.diary.ui.recipe.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.sum
import com.maksimowiczm.foodyou.feature.diary.ui.component.CaloriesProgressIndicator
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummary
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults.measurementString
import com.maksimowiczm.foodyou.feature.diary.ui.component.MeasurementSummaryDefaults.measurementStringShort
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientsList
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientsRow
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.CreateFoodProductMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.EditFoodProductMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.measurementGraph
import com.maksimowiczm.foodyou.feature.diary.ui.product.CreateProduct
import com.maksimowiczm.foodyou.feature.diary.ui.product.EditProduct
import com.maksimowiczm.foodyou.feature.diary.ui.product.productGraph
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.cases.MeasuredIngredient
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.model.Ingredient
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.ui.component.BackHandler
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val CREATE_RECIPE_SCREEN = "create_recipe"
private const val SEARCH_SCREEN = "search"
private const val CREATE_PRODUCT_SCREEN = "create_product"

@Suppress("ktlint:compose:vm-forwarding-check")
@Composable
fun CreateRecipeDialog(
    onClose: () -> Unit,
    onCreate: (recipeId: Long) -> Unit,
    onIncompleteProductClick: (productId: Long) -> Unit,
    onGoToOpenFoodFactsSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateRecipeViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val ingredients by viewModel.ingredients.collectAsStateWithLifecycle()
    val formState = rememberCreateRecipeDialogState()

    // Use NavHost to handle predictive back navigation
    NavHost(
        navController = navController,
        startDestination = CREATE_RECIPE_SCREEN
    ) {
        crossfadeComposable(CREATE_RECIPE_SCREEN) {
            CreateRecipeDialog(
                state = formState,
                ingredients = ingredients,
                onClose = onClose,
                onIngredientAdd = {
                    navController.navigate(SEARCH_SCREEN)
                },
                onIncompleteProductClick = onIncompleteProductClick,
                modifier = modifier
            )
        }
        crossfadeComposable(SEARCH_SCREEN) {
            IngredientSearch(
                onBack = {
                    navController.popBackStack(route = SEARCH_SCREEN, inclusive = true)
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
                onProductClick = {
                    navController.navigate(
                        route = CreateFoodProductMeasurement(it.productId),
                        navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                viewModel = viewModel
            )
        }
        productGraph(
            onCreateClose = { navController.popBackStack<CreateProduct>(inclusive = true) },
            onCreateSuccess = {
                navController.navigate(
                    route = CreateFoodProductMeasurement(it),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            onEditClose = { navController.popBackStack<EditProduct>(inclusive = true) },
            onEditSuccess = { navController.popBackStack<EditProduct>(inclusive = true) }
        )
        measurementGraph(
            onCreate = { foodId, weightMeasurement ->
                viewModel.onAddIngredient(
                    MeasuredIngredient(
                        productId = foodId as FoodId.Product,
                        weightMeasurement = weightMeasurement
                    )
                )
                navController.navigate(
                    route = CREATE_RECIPE_SCREEN,
                    navOptions = navOptions {
                        popUpTo(CREATE_RECIPE_SCREEN) {
                            inclusive = true
                        }
                    }
                )
            },
            onCreateBack = {
                navController.popBackStack<CreateFoodProductMeasurement>(inclusive = true)
            },
            onEdit = { measurementId, weightMeasurement ->
                // TODO
            },
            onEditBack = {
                navController.popBackStack<EditFoodProductMeasurement>(inclusive = true)
            },
            onEditFood = {
                when (it) {
                    is FoodId.Product -> navController.navigate(
                        route = EditProduct(it.productId),
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )

                    is FoodId.Recipe -> TODO()
                }
            },
            onDeleteFood = {
                // TODO
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRecipeDialog(
    state: CreateRecipeDialogState,
    ingredients: List<Ingredient>,
    onClose: () -> Unit,
    onIngredientAdd: () -> Unit,
    onIncompleteProductClick: (productId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = { showDiscardDialog = false },
            onConfirm = onClose
        )
    }
    val isDirty = remember(state.nameTextFieldState.textFieldState.text, ingredients) {
        state.nameTextFieldState.textFieldState.text.isNotBlank() || ingredients.isNotEmpty()
    }

    val handleClose = {
        if (isDirty) {
            showDiscardDialog = true
        } else {
            onClose()
        }
    }

    BackHandler(
        enabled = isDirty
    ) {
        showDiscardDialog = true
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topBar = @Composable {
        TopAppBar(
            title = { Text(stringResource(Res.string.headline_create_recipe)) },
            navigationIcon = {
                IconButton(
                    onClick = handleClose
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.action_close)
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        // TODO
                    }
                ) {
                    Text(stringResource(Res.string.action_create))
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.headline_general),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                GeneralSection(
                    state = state,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
            }

            item {
                Text(
                    text = stringResource(Res.string.headline_ingredients),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            ingredientsSection(
                onAddIngredient = onIngredientAdd,
                ingredients = ingredients,
                onIngredientClick = {
                    // TODO
                }
            )

            if (ingredients.isNotEmpty()) {
                item {
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    Text(
                        text = stringResource(Res.string.headline_summary),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    SummarySection(
                        ingredients = ingredients,
                        onIncompleteProductClick = onIncompleteProductClick,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun GeneralSection(state: CreateRecipeDialogState, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            state = state.nameTextFieldState.textFieldState,
            isError = state.nameTextFieldState.error != null,
            label = { Text(stringResource(Res.string.product_name)) },
            supportingText = {
                Text("* " + stringResource(Res.string.neutral_required))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        TextField(
            state = state.servingsTextFieldState.textFieldState,
            isError = state.servingsTextFieldState.error != null,
            label = { Text(stringResource(Res.string.recipe_servings)) },
            supportingText = {
                Text(stringResource(Res.string.description_recipe_servings))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
    }
}

private fun LazyListScope.ingredientsSection(
    onAddIngredient: () -> Unit,
    ingredients: List<Ingredient>,
    onIngredientClick: (Ingredient) -> Unit
) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddIngredient() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = onAddIngredient,
                modifier = Modifier.clearAndSetSemantics { },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = stringResource(Res.string.action_add_ingredient),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    items(
        items = ingredients
    ) { model ->
        model.ListItem(
            onClick = { onIngredientClick(model) }
        )
    }
}

@Composable
private fun SummarySection(
    ingredients: List<Ingredient>,
    onIncompleteProductClick: (productId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val nutrients = remember(ingredients) {
        ingredients.map { it.product.nutrients }.sum()
    }
    val weight = remember(ingredients) {
        ingredients.map { it.weightMeasurement.getWeight(it.product) }.sum()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(
                Res.string.in_x_weight_unit,
                weight.formatClipZeros(),
                stringResource(Res.string.unit_gram_short)
            ),
            style = MaterialTheme.typography.labelLarge
        )

        CaloriesProgressIndicator(
            proteins = nutrients.proteins,
            carbohydrates = nutrients.carbohydrates,
            fats = nutrients.fats,
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        NutrientsList(
            nutrients = nutrients,
            incompleteValue = { value ->
                {
                    when (value.value) {
                        null -> Text(
                            text = stringResource(Res.string.not_available_short),
                            color = MaterialTheme.colorScheme.outline
                        )

                        else -> Text(
                            text =
                            "* " + value.value.formatClipZeros() +
                                stringResource(Res.string.unit_gram_short),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        )

        val incompleteProducts = ingredients.filter { it.product.nutrients.isComplete.not() }

        if (incompleteProducts.isNotEmpty()) {
            Text(
                text = "* " + stringResource(Res.string.description_incomplete_nutrition_data),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = stringResource(Res.string.headline_incomplete_products),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )

            incompleteProducts.forEach { ingredient ->
                Text(
                    text = ingredient.product.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.clickable(
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null,
                        onClick = {
                            onIncompleteProductClick(ingredient.product.id.productId)
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun Ingredient.ListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val weight = weightMeasurement.getWeight(product)
    val proteins = product.nutrients.proteins * weight / 100
    val carbohydrates = product.nutrients.carbohydrates * weight / 100
    val fats = product.nutrients.fats * weight / 100

    ListItem(
        headlineContent = { Text(product.name) },
        modifier = modifier.clickable { onClick() },
        overlineContent = { product.brand?.let { Text(it) } },
        supportingContent = {
            Column {
                NutrientsRow(
                    proteins = proteins.roundToInt(),
                    carbohydrates = carbohydrates.roundToInt(),
                    fats = fats.roundToInt(),
                    modifier = Modifier.fillMaxWidth()
                )

                with(weightMeasurement) {
                    MeasurementSummary(
                        measurementString = measurementString(weight),
                        measurementStringShort = measurementStringShort,
                        caloriesString = MeasurementSummaryDefaults.caloriesString(
                            calories.roundToInt()
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}

@Composable
private fun DiscardDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(Res.string.action_discard))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        text = {
            Text(stringResource(Res.string.question_discard_recipe))
        }
    )
}
