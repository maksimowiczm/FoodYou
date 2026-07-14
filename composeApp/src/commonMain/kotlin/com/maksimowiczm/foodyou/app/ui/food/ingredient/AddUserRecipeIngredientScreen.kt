package com.maksimowiczm.foodyou.app.ui.food.ingredient

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.maksimowiczm.foodyou.app.ui.common.component.QuantityInput
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.headline
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsHeadline
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsImage
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsTopBar
import com.maksimowiczm.foodyou.app.ui.food.details.UserFoodMenu
import com.maksimowiczm.foodyou.app.ui.food.details.UserFoodNote
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.app.ui.food.details.userrecipe.UserRecipeDetailsUiEvent
import com.maksimowiczm.foodyou.app.ui.food.details.userrecipe.UserRecipeDetailsViewModel
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.Quantity
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
    quantityState: AddIngredientQuantityState,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

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

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

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
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = contentPadding.add(bottom = 128.dp),
        ) {
            item { FoodDetailsHeadline(headline = headline) }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                FoodDetailsImage(
                    image = recipe?.image?.let { resolveBlob(it) },
                    showPlaceholder = recipe == null,
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                QuantityInput(
                    entries = quantityState.quantityTypes,
                    selectedQuantity = quantityState.selectedQuantityType,
                    onQuantity = quantityState::onSelectedQuantityTypeChange,
                    formField = quantityState.formField,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            if (scaledNutritionFacts != null) {
                item {
                    AddIngredientNutrients(
                        nutritionFacts = scaledNutritionFacts,
                        quantities = quantityState.quantitySuggestions,
                        servingQuantity = servingQuantity,
                        packageQuantity = packageQuantity,
                        onSelectQuantity = quantityState::onSelectQuantity,
                        expanded = expanded.value,
                        onExpandedChange = { expanded.value = it },
                        expandingEnabled = expandingEnabled,
                    )
                }
            }
            if (recipe?.note != null) {
                item {
                    Spacer(Modifier.height(8.dp))
                    UserFoodNote(
                        note = recipe.note,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}
