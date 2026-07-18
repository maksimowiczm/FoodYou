package com.maksimowiczm.foodyou.app.ui.food.details.userrecipe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.headline
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsHeadline
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsImage
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsNutrients
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsTopBar
import com.maksimowiczm.foodyou.app.ui.food.details.UserFoodMenu
import com.maksimowiczm.foodyou.app.ui.food.details.UserFoodNote
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityCalculator
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.food.isIncomplete
import com.maksimowiczm.foodyou.common.domain.food.scale
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.getOrNull
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UserRecipeDetailsScreen(
    identity: UserRecipeIdentity,
    initialQuantity: Quantity?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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

    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val userRecipe by viewModel.userRecipe.collectAsStateWithLifecycle()

    UserRecipeDetailsScreen(
        isFavorite = isFavorite,
        recipe = userRecipe,
        initialQuantity = initialQuantity,
        onBack = onBack,
        onEdit = onEdit,
        onDelete = viewModel::delete,
        onSetFavorite = viewModel::setFavorite,
        onNavigateToIngredient = onNavigateToIngredient,
        modifier = modifier,
    )
}

@Composable
private fun UserRecipeDetailsScreen(
    isFavorite: Boolean?,
    recipe: UserRecipe?,
    initialQuantity: Quantity?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onNavigateToIngredient: (FoodCompositionComponentIdentity, Quantity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

    var expanded by rememberNutrientExpanded()
    val expandingEnabled =
        remember(recipe?.nutritionFacts) {
            if (recipe?.nutritionFacts == null) return@remember false
            (Nutrient.all - Nutrient.basic).any { recipe.nutritionFacts[it].value != null }
        }

    val servingWeight = recipe?.servingWeight
    val totalWeight = recipe?.totalWeight

    var quantity by
        rememberSerializable(initialQuantity, servingWeight, totalWeight) {
            val default =
                initialQuantity
                    ?: if (servingWeight != null) ServingQuantity(1.0)
                    else AbsoluteQuantity.Weight(100.grams)
            mutableStateOf(default)
        }
    val quantitySuggestions =
        remember(initialQuantity, servingWeight, totalWeight) {
            if (recipe == null) return@remember emptyList()
            else
                buildList {
                    add(AbsoluteQuantity.Weight(100.grams))
                    if (initialQuantity != null) add(initialQuantity)
                    if (servingWeight != null) add(ServingQuantity(1.0))
                    if (totalWeight != null) add(PackageQuantity(1.0))
                }
                    .distinct()
        }

    val headline = remember(recipe?.name, nameSelector) { recipe?.headline(nameSelector) }

    val ingredientScalingFactor =
        remember(recipe?.totalWeight, recipe?.servingWeight, quantity) {
            val totalWeight = recipe?.totalWeight?.takeIf { it.grams > 0 } ?: return@remember 1.0
            val servingWeight = recipe.servingWeight

            val selectedAbsoluteQuantity =
                QuantityCalculator.calculateAbsoluteQuantity(
                        suggestedQuantity = quantity,
                        packageQuantity = AbsoluteQuantity.Weight(totalWeight),
                        servingQuantity = AbsoluteQuantity.Weight(servingWeight),
                    )
                    .getOrNull() ?: return@remember 1.0

            when (selectedAbsoluteQuantity) {
                is AbsoluteQuantity.Weight -> selectedAbsoluteQuantity.weight / totalWeight
                is AbsoluteQuantity.Volume -> 1.0
            }
        }

    val scaledNutritionFacts =
        remember(recipe?.nutritionFacts, servingWeight, totalWeight, quantity) {
            recipe
                ?.nutritionFacts
                ?.scale(
                    packageQuantity = totalWeight?.let { AbsoluteQuantity.Weight(it) },
                    servingQuantity = servingWeight?.let { AbsoluteQuantity.Weight(it) },
                    quantity = quantity,
                )
                ?.getOrNull()
        }
    val anyNutrientIsMissing =
        remember(recipe?.nutritionFacts) {
            if (recipe?.nutritionFacts == null) return@remember false
            recipe.nutritionFacts.asMap().any { it.value.isIncomplete() } ||
                recipe.nutritionFacts.energy.isIncomplete()
        }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodDetailsTopBar(
                onBack = onBack,
                title = headline,
                actions = {
                    FavoriteIconButton(
                        favorite = isFavorite ?: false,
                        onChange = onSetFavorite,
                        enabled = recipe != null,
                    )
                    UserFoodMenu(onEdit = onEdit, onDelete = onDelete, enabled = recipe != null)
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = contentPadding.add(bottom = 8.dp),
        ) {
            item { FoodDetailsHeadline(headline = headline) }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                FoodDetailsImage(
                    image = recipe?.image?.let { resolveBlob(it) },
                    showPlaceholder = recipe == null,
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            if (recipe != null) {
                item {
                    recipe.components.forEach { component ->
                        RecipeIngredientListItem(
                            component = component,
                            scalingFactor = ingredientScalingFactor,
                            onClick = { onNavigateToIngredient(component.identity, it) },
                        )
                    }
                }
            }
            if (scaledNutritionFacts != null) {
                item {
                    Column {
                        FoodDetailsNutrients(
                            nutritionFacts = scaledNutritionFacts,
                            quantities = quantitySuggestions,
                            selectedQuantity = quantity,
                            servingQuantity = servingWeight?.let { AbsoluteQuantity.Weight(it) },
                            packageQuantity = totalWeight?.let { AbsoluteQuantity.Weight(it) },
                            onSelectQuantity = { quantity = it },
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            expandingEnabled = expandingEnabled,
                        )
                        if (anyNutrientIsMissing) {
                            Text(
                                text =
                                    "* " +
                                        stringResource(
                                            Res.string.description_incomplete_nutrition_data
                                        ),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
            if (recipe?.note != null) {
                item { Spacer(Modifier.height(8.dp)) }
                item {
                    UserFoodNote(
                        note = recipe.note,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}
