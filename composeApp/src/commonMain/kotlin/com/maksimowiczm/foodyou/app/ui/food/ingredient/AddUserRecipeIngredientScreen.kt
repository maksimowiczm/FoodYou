package com.maksimowiczm.foodyou.app.ui.food.ingredient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.maksimowiczm.foodyou.app.ui.common.component.QuantityInput
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.headline
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsHeadline
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsImage
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsTopBar
import com.maksimowiczm.foodyou.app.ui.food.details.UserFoodMenu
import com.maksimowiczm.foodyou.app.ui.food.details.UserFoodNote
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.app.ui.food.details.userrecipe.RecipeIngredientListItem
import com.maksimowiczm.foodyou.app.ui.food.details.userrecipe.UserRecipeDetailsUiEvent
import com.maksimowiczm.foodyou.app.ui.food.details.userrecipe.UserRecipeDetailsViewModel
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityCalculator
import com.maksimowiczm.foodyou.common.domain.food.isIncomplete
import com.maksimowiczm.foodyou.common.domain.food.scale
import com.maksimowiczm.foodyou.common.getOrNull
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddUserRecipeIngredientScreen(
    onBack: () -> Unit,
    onAdd: (Quantity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    identity: UserRecipeIdentity,
    initialQuantity: Quantity,
    onNavigateToIngredient: (FoodCompositionComponentIdentity, Quantity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: UserRecipeDetailsViewModel =
        koinViewModel(parameters = { parametersOf(identity) })

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            UserRecipeDetailsUiEvent.Deleted -> onDelete()
        }
    }

    val recipe by viewModel.userRecipe.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    val packageQuantity =
        remember(recipe) { recipe?.totalWeight?.let { AbsoluteQuantity.Weight(it) } }
    val servingQuantity =
        remember(recipe) { recipe?.servingWeight?.let { AbsoluteQuantity.Weight(it) } }

    val quantityState =
        rememberAddIngredientQuantityState(
            initialQuantity = initialQuantity,
            servingQuantity = servingQuantity,
            packageQuantity = packageQuantity,
            isLiquid = false,
        )

    AddUserRecipeIngredientScreen(
        recipe = recipe,
        isFavorite = isFavorite,
        onSetFavorite = viewModel::setFavorite,
        onBack = onBack,
        onAdd = { onAdd(quantityState.quantity) },
        onEdit = onEdit,
        onDelete = viewModel::delete,
        onNavigateToIngredient = onNavigateToIngredient,
        modifier = modifier,
        quantityState = quantityState,
    )
}

@Composable
private fun AddUserRecipeIngredientScreen(
    recipe: UserRecipe?,
    isFavorite: Boolean?,
    onSetFavorite: (Boolean) -> Unit,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onNavigateToIngredient: (FoodCompositionComponentIdentity, Quantity) -> Unit,
    quantityState: AddIngredientQuantityState,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current
    val lazyListState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    var focusRequested by rememberSaveable { mutableStateOf(false) }

    val expanded = rememberNutrientExpanded()
    val expandingEnabled =
        remember(recipe?.nutritionFacts) {
            val nutritionFacts = recipe?.nutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { nutritionFacts[it].value != null }
        }

    val headline = remember(recipe, nameSelector) { recipe?.headline(nameSelector) }

    val packageQuantity =
        remember(recipe) { recipe?.totalWeight?.let { AbsoluteQuantity.Weight(it) } }
    val servingQuantity =
        remember(recipe) { recipe?.servingWeight?.let { AbsoluteQuantity.Weight(it) } }

    val scaledNutritionFacts =
        remember(recipe?.nutritionFacts, packageQuantity, servingQuantity, quantityState.quantity) {
            recipe
                ?.nutritionFacts
                ?.scale(packageQuantity, servingQuantity, quantityState.quantity)
                ?.getOrNull()
        }

    val ingredientScalingFactor =
        remember(recipe, quantityState.quantity) {
            val totalWeight = recipe?.totalWeight?.takeIf { it.grams > 0 } ?: return@remember 1.0
            val servingWeight = recipe.servingWeight

            val selectedAbsoluteQuantity =
                QuantityCalculator.calculateAbsoluteQuantity(
                        suggestedQuantity = quantityState.quantity,
                        packageQuantity = AbsoluteQuantity.Weight(totalWeight),
                        servingQuantity = AbsoluteQuantity.Weight(servingWeight),
                    )
                    .getOrNull() ?: return@remember 1.0

            when (selectedAbsoluteQuantity) {
                is AbsoluteQuantity.Weight -> selectedAbsoluteQuantity.weight / totalWeight
                is AbsoluteQuantity.Volume -> 1.0
            }
        }

    val stringedQuantities =
        quantityState.quantitySuggestions.mapNotNull { quantity ->
            quantity.stringResource(packageQuantity, servingQuantity).getOrNull()?.let {
                quantity to it
            }
        }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val anyNutrientIsMissing =
        remember(recipe?.nutritionFacts) {
            if (recipe?.nutritionFacts == null) return@remember false
            recipe.nutritionFacts.asMap().any { it.value.isIncomplete() } ||
                recipe.nutritionFacts.energy.isIncomplete()
        }

    LaunchedEffect(Unit) {
        if (!focusRequested) {
            val quantityInputIndex =
                1 +
                    if (recipe?.image != null) 1
                    else
                        0 +
                            if (recipe != null && recipe.components.isNotEmpty()) 1
                            else 0 + if (stringedQuantities.isNotEmpty()) 1 else 0
            lazyListState.animateScrollToItem(quantityInputIndex)
            focusRequester.requestFocus()
            focusRequested = true
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodDetailsTopBar(
                onBack = onBack,
                title = headline,
                scrollBehavior = scrollBehavior,
                actions = {
                    FavoriteIconButton(favorite = isFavorite ?: false, onChange = onSetFavorite)
                    UserFoodMenu(onEdit = onEdit, onDelete = onDelete)
                },
            )
        },
        floatingActionButton = {
            LargeExtendedFloatingActionButton(
                onClick = onAdd,
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible =
                            recipe != null &&
                                !LocalNavAnimatedContentScope.current.transition.isRunning &&
                                quantityState.formField.error == null,
                        alignment = Alignment.BottomEnd,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
                )
                Spacer(Modifier.width(16.dp))
                Text(stringResource(Res.string.action_add))
            }
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).imePadding(),
            state = lazyListState,
            contentPadding = contentPadding.add(bottom = 128.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FoodDetailsHeadline(
                    headline = headline,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
                Spacer(Modifier.height(8.dp))
            }
            if (recipe?.image != null) {
                item {
                    FoodDetailsImage(
                        image = resolveBlob(recipe.image),
                        showPlaceholder = false,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                }
            }
            if (recipe != null && recipe.components.isNotEmpty()) {
                val hasNestedRecipe =
                    recipe.components.any {
                        it.identity is FoodCompositionComponentIdentity.Composite
                    }

                item {
                    Column(
                        modifier =
                            Modifier.padding(horizontal = 8.dp).clip(MaterialTheme.shapes.large),
                        verticalArrangement =
                            if (hasNestedRecipe) Arrangement.spacedBy(8.dp)
                            else Arrangement.spacedBy(2.dp),
                    ) {
                        recipe.components.forEach { component ->
                            RecipeIngredientListItem(
                                component = component,
                                scalingFactor = ingredientScalingFactor,
                                onNavigateToIngredient = onNavigateToIngredient,
                                modifier = Modifier.fillMaxWidth(),
                                unwrap = hasNestedRecipe,
                            )
                        }
                    }
                }
            }
            if (stringedQuantities.isNotEmpty()) {
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    ) {
                        items(stringedQuantities) { (quantity, text) ->
                            AssistChip(
                                onClick = { quantityState.onSelectQuantity(quantity) },
                                label = { Text(text) },
                            )
                        }
                    }
                }
            }
            item {
                QuantityInput(
                    entries = quantityState.quantityTypes,
                    selectedQuantity = quantityState.selectedQuantityType,
                    onQuantity = quantityState::onSelectedQuantityTypeChange,
                    formField = quantityState.formField,
                    modifier = Modifier.padding(horizontal = 8.dp).focusRequester(focusRequester),
                )
            }
            if (scaledNutritionFacts != null) {
                item {
                    Column {
                        AddIngredientNutrients(
                            nutritionFacts = scaledNutritionFacts,
                            expanded = expanded.value,
                            onExpandedChange = { expanded.value = it },
                            expandingEnabled = expandingEnabled,
                        )
                        if (anyNutrientIsMissing) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text =
                                    "* " +
                                        stringResource(
                                            Res.string.description_incomplete_nutrition_data
                                        ),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
            if (recipe?.note != null) {
                item {
                    UserFoodNote(
                        note = recipe.note,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            }
        }
    }
}
