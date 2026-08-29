package com.maksimowiczm.foodyou.features.fooddetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsWithSuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodImage
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodIngredients
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodNote
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.UserFoodMenu
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientExpanded
import com.maksimowiczm.foodyou.capabilities.fooddetails.userrecipe.UserRecipeDetailsUiEvent
import com.maksimowiczm.foodyou.capabilities.fooddetails.userrecipe.UserRecipeDetailsViewModel
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
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
        koinViewModel(parameters = { parametersOf(identity, initialQuantity) })

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            UserRecipeDetailsUiEvent.Deleted -> onDelete()
        }
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val nameSelector = LocalFoodNameSelector.current

    if (uiState.recipe != null) {
        UserRecipeDetailsScreenContent(
            headline = uiState.recipe.headline(nameSelector),
            isFavorite = uiState.isFavorite,
            image = uiState.recipe.image?.let { resolveBlob(it) },
            note = uiState.recipe.note,
            components = uiState.recipe.components,
            ingredientScalingFactor = uiState.ingredientScalingFactor,
            suggestions = uiState.suggestions,
            selectedQuantity = uiState.selectedQuantity,
            scaledNutritionFacts = uiState.scaledNutritionFacts,
            packageQuantity = AbsoluteQuantity.Weight(uiState.recipe.totalWeight),
            servingQuantity = AbsoluteQuantity.Weight(uiState.recipe.servingWeight),
            onBack = onBack,
            onEdit = onEdit,
            onDelete = viewModel::delete,
            onSetFavorite = viewModel::setFavorite,
            onSelectQuantity = viewModel::selectQuantity,
            onNavigateToIngredient = onNavigateToIngredient,
            modifier = modifier,
        )
    }
}

@Composable
private fun UserRecipeDetailsScreenContent(
    headline: String?,
    isFavorite: Boolean,
    image: FileUri?,
    note: String?,
    components: List<FoodCompositionComponent>,
    ingredientScalingFactor: Double,
    suggestions: List<Quantity>,
    selectedQuantity: Quantity?,
    scaledNutritionFacts: NutritionFacts?,
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    onNavigateToIngredient: (FoodCompositionComponentIdentity, Quantity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var expanded by rememberNutrientExpanded()
    val expandingEnabled =
        remember(scaledNutritionFacts) {
            val scaledNutritionFacts = scaledNutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { scaledNutritionFacts[it].value != null }
        }

    val anyNutrientIsMissing =
        remember(scaledNutritionFacts) { scaledNutritionFacts?.isIncomplete() == true }

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodScreenTopBar(
                onBack = onBack,
                title = headline,
                actions = {
                    FavoriteIconButton(
                        isFavorite = isFavorite,
                        onChange = onSetFavorite,
                    )
                    UserFoodMenu(onEdit = onEdit, onDelete = onDelete)
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = contentPadding.add(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                FoodHeadline(headline, Modifier.padding(horizontal = 8.dp))
            }
            if (image != null)
                item {
                    FoodImage(image, Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            if (components.isNotEmpty())
                item {
                    FoodIngredients(
                        components = components,
                        ingredientScalingFactor = ingredientScalingFactor,
                        onNavigateToIngredient = onNavigateToIngredient,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            if (scaledNutritionFacts != null)
                item {
                    Column {
                        FoodDetailsNutrientsWithSuggestions(
                            scaledNutritionFacts = scaledNutritionFacts,
                            suggestions = suggestions,
                            selectedQuantity = selectedQuantity,
                            packageQuantity = packageQuantity,
                            servingQuantity = servingQuantity,
                            onSelectQuantity = onSelectQuantity,
                            expanded = if (!expandingEnabled) false else expanded,
                            onExpandedChange = { expanded = it },
                            expandingEnabled = expandingEnabled,
                            contentPadding = PaddingValues(horizontal = 8.dp),
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
            if (note != null)
                item {
                    FoodNote(note, Modifier.padding(horizontal = 8.dp))
                }
        }
    }
}
