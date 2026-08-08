package com.maksimowiczm.foodyou.features.food.ingredient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.maksimowiczm.foodyou.capabilities.food.FoodDetailsNutrientsCompact
import com.maksimowiczm.foodyou.capabilities.food.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScope
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithImage
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithIngredients
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithNote
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithQuantityInput
import com.maksimowiczm.foodyou.capabilities.food.Headline
import com.maksimowiczm.foodyou.capabilities.food.Image
import com.maksimowiczm.foodyou.capabilities.food.Ingredients
import com.maksimowiczm.foodyou.capabilities.food.Note
import com.maksimowiczm.foodyou.capabilities.food.QuantityInput
import com.maksimowiczm.foodyou.capabilities.food.QuantitySuggestions
import com.maksimowiczm.foodyou.capabilities.food.UserFoodMenu
import com.maksimowiczm.foodyou.capabilities.food.rememberNutrientExpanded
import com.maksimowiczm.foodyou.capabilities.food.rememberQuantityFormField
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.amount
import com.maksimowiczm.foodyou.features.food.details.userrecipe.UserRecipeDetailsUiEvent
import com.maksimowiczm.foodyou.features.food.details.userrecipe.UserRecipeDetailsViewModel
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
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
    val viewModel: UserRecipeDetailsViewModel = koinViewModel {
        parametersOf(identity, initialQuantity)
    }

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            UserRecipeDetailsUiEvent.Deleted -> onDelete()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userRecipe = uiState.recipe

    val nameSelector = LocalFoodNameSelector.current
    val defaultValue = remember(initialQuantity) { initialQuantity.amount.formatCompact() }
    val formField =
        rememberQuantityFormField(
            defaultValue,
            defaultValue = defaultValue,
        )

    LaunchedEffect(formField.textFieldState.text, uiState.selectedQuantityType) {
        viewModel.selectQuantity(
            formField.textFieldState.text.toString().toDoubleOrNull(),
            uiState.selectedQuantityType,
        )
    }

    val image = userRecipe?.image?.let { resolveBlob(it) }

    val scope =
        remember(uiState, nameSelector, image, formField) {
            val recipe = uiState.recipe ?: return@remember null

            AddIngredientUserRecipeScope(
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
                types = uiState.quantityTypes,
                selectedType = uiState.selectedQuantityType ?: QuantityType.Gram,
                formField = formField,
            )
        }

    scope?.Screen(
        onBack = onBack,
        onAdd = { scope.selectedQuantity?.let { onAdd(it) } },
        onEdit = onEdit,
        onDelete = viewModel::delete,
        onSetFavorite = viewModel::setFavorite,
        onSelectQuantity = {
            viewModel.selectQuantity(it)
            formField.textFieldState.setTextAndPlaceCursorAtEnd(it.amount.formatCompact())
        },
        onSelectQuantityType = viewModel::selectQuantityType,
        onNavigateToIngredient = onNavigateToIngredient,
        modifier = modifier,
    )
}

@Immutable
private data class AddIngredientUserRecipeScope(
    override val headline: String?,
    override val isFavorite: Boolean,
    override val image: FileUri?,
    override val note: String?,
    override val components: List<FoodCompositionComponent>,
    override val ingredientScalingFactor: Double,
    override val suggestions: List<Quantity>,
    override val selectedQuantity: Quantity?,
    override val scaledNutritionFacts: NutritionFacts?,
    override val packageQuantity: AbsoluteQuantity?,
    override val servingQuantity: AbsoluteQuantity?,
    override val types: List<QuantityType>,
    override val selectedType: QuantityType,
    override val formField: FormField,
) :
    FoodUiScope,
    FoodUiScopeWithImage,
    FoodUiScopeWithNote,
    FoodUiScopeWithIngredients,
    FoodUiScopeWithQuantityInput

@Composable
private fun AddIngredientUserRecipeScope.Screen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    onSelectQuantityType: (QuantityType) -> Unit,
    onNavigateToIngredient: (FoodCompositionComponentIdentity, Quantity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    var focusRequested by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!focusRequested) {
            val _ = runCatching { focusRequester.requestFocus() }
            focusRequested = true
        }
    }

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
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FoodScreenTopBar(
                onBack = onBack,
                title = headline,
                actions = {
                    FavoriteIconButton(favorite = isFavorite, onChange = onSetFavorite)
                    UserFoodMenu(onEdit = onEdit, onDelete = onDelete)
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            LargeExtendedFloatingActionButton(
                onClick = onAdd,
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible =
                            !LocalNavAnimatedContentScope.current.transition.isRunning &&
                                formField.error == null,
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
            modifier = Modifier.imePadding(),
            contentPadding = contentPadding.add(top = 26.dp, bottom = 128.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Headline(Modifier.padding(horizontal = 8.dp))
            }
            if (image != null)
                item {
                    Image(Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            if (components.isNotEmpty())
                item {
                    Ingredients(
                        onNavigateToIngredient = onNavigateToIngredient,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            if (suggestions.isNotEmpty())
                item {
                    QuantitySuggestions(
                        onSelectQuantity = onSelectQuantity,
                        modifier = Modifier.height(32.dp),
                    )
                }
            item {
                QuantityInput(
                    onSelectType = onSelectQuantityType,
                    modifier = Modifier.padding(horizontal = 8.dp).focusRequester(focusRequester),
                )
            }
            if (scaledNutritionFacts != null)
                item {
                    Column {
                        FoodDetailsNutrientsCompact(
                            expanded = if (!expandingEnabled) false else expanded,
                            onExpandedChange = { expanded = it },
                            expandingEnabled = expandingEnabled,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
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
                    Note(Modifier.padding(horizontal = 8.dp))
                }
        }
    }
}
