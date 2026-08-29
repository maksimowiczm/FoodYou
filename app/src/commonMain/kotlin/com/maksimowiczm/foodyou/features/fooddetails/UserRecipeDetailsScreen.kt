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

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val nameSelector = LocalFoodNameSelector.current
    val image = uiState.recipe?.image?.let { resolveBlob(it) }

    val scope =
        remember(uiState, nameSelector, image) {
            val recipe = uiState.recipe ?: return@remember null

            UserRecipeScope(
                headline = recipe.headline(nameSelector),
                isFavorite = uiState.isFavorite,
                image = image,
                note = recipe.note,
                components = recipe.components,
                ingredientScalingFactor = uiState.ingredientScalingFactor,
                suggestions = uiState.suggestions,
                selectedQuantity = uiState.selectedQuantity,
                scaledNutritionFacts = uiState.scaledNutritionFacts,
                packageQuantity = AbsoluteQuantity.Weight(recipe.totalWeight),
                servingQuantity = AbsoluteQuantity.Weight(recipe.servingWeight),
            )
        }

    scope?.let {
        UserRecipeDetailsScreenContent(
            scope = it,
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

@Immutable
private data class UserRecipeScope(
    val headline: String?,
    val isFavorite: Boolean,
    val image: FileUri?,
    val note: String?,
    val components: List<FoodCompositionComponent>,
    val ingredientScalingFactor: Double,
    val suggestions: List<Quantity>,
    val selectedQuantity: Quantity?,
    val scaledNutritionFacts: NutritionFacts?,
    val packageQuantity: AbsoluteQuantity?,
    val servingQuantity: AbsoluteQuantity?,
)

@Composable
private fun UserRecipeDetailsScreenContent(
    scope: UserRecipeScope,
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
        remember(scope.scaledNutritionFacts) {
            val scaledNutritionFacts = scope.scaledNutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { scaledNutritionFacts[it].value != null }
        }

    val anyNutrientIsMissing =
        remember(scope.scaledNutritionFacts) { scope.scaledNutritionFacts?.isIncomplete() == true }

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodScreenTopBar(
                onBack = onBack,
                title = scope.headline,
                actions = {
                    FavoriteIconButton(
                        isFavorite = scope.isFavorite,
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
                FoodHeadline(scope.headline, Modifier.padding(horizontal = 8.dp))
            }
            if (scope.image != null)
                item {
                    FoodImage(scope.image, Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            if (scope.components.isNotEmpty())
                item {
                    FoodIngredients(
                        components = scope.components,
                        ingredientScalingFactor = scope.ingredientScalingFactor,
                        onNavigateToIngredient = onNavigateToIngredient,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            if (scope.scaledNutritionFacts != null)
                item {
                    Column {
                        FoodDetailsNutrientsWithSuggestions(
                            scaledNutritionFacts = scope.scaledNutritionFacts,
                            suggestions = scope.suggestions,
                            selectedQuantity = scope.selectedQuantity,
                            packageQuantity = scope.packageQuantity,
                            servingQuantity = scope.servingQuantity,
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
            if (scope.note != null)
                item {
                    FoodNote(scope.note, Modifier.padding(horizontal = 8.dp))
                }
        }
    }
}
