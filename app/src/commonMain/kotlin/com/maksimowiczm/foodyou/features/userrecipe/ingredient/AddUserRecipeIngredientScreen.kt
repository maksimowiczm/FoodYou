package com.maksimowiczm.foodyou.features.userrecipe.ingredient

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
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsCompact
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodImage
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodIngredients
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodNote
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantityInput
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantitySuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.UserFoodMenu
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientExpanded
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberQuantityFormField
import com.maksimowiczm.foodyou.capabilities.fooddetails.userrecipe.UserRecipeDetailsUiEvent
import com.maksimowiczm.foodyou.capabilities.fooddetails.userrecipe.UserRecipeDetailsViewModel
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.amount
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
    onNavigateToIngredient: (FoodSnapshotId, Quantity) -> Unit,
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
    val selectedType = uiState.selectedQuantityType

    val nameSelector = LocalFoodNameSelector.current
    val defaultValue = remember(initialQuantity) { initialQuantity.amount.formatCompact() }
    val formField =
        rememberQuantityFormField(
            defaultValue,
            defaultValue = defaultValue,
        )

    if (userRecipe != null && selectedType != null) {
        LaunchedEffect(formField.textFieldState.text, selectedType) {
            viewModel.selectQuantity(
                formField.textFieldState.text.toString().toDoubleOrNull(),
                selectedType,
            )
        }

        AddUserRecipeIngredientScreenContent(
            headline = userRecipe.headline(nameSelector),
            isFavorite = uiState.isFavorite,
            image = userRecipe.image?.let { resolveBlob(it) },
            note = userRecipe.note,
            components = userRecipe.components,
            ingredientScalingFactor = uiState.ingredientScalingFactor,
            suggestions = uiState.suggestions,
            scaledNutritionFacts = uiState.scaledNutritionFacts,
            packageQuantity = AbsoluteQuantity.Weight(userRecipe.totalWeight),
            servingQuantity = AbsoluteQuantity.Weight(userRecipe.servingWeight),
            types = uiState.quantityTypes,
            selectedType = selectedType,
            formField = formField,
            onBack = onBack,
            onAdd = { uiState.selectedQuantity?.let { onAdd(it) } },
            onEdit = onEdit,
            onDelete = viewModel::delete,
            onSetFavorite = viewModel::setFavorite,
            onSelectQuantity = { quantity ->
                viewModel.selectQuantity(quantity)
                formField.textFieldState.setTextAndPlaceCursorAtEnd(quantity.amount.formatCompact())
            },
            onSelectQuantityType = viewModel::selectQuantityType,
            onNavigateToIngredient = onNavigateToIngredient,
            modifier = modifier,
        )
    }
}

@Composable
private fun AddUserRecipeIngredientScreenContent(
    headline: String?,
    isFavorite: Boolean,
    image: FileUri?,
    note: String?,
    components: List<MeasuredFoodSnapshot>,
    ingredientScalingFactor: Double,
    suggestions: List<Quantity>,
    scaledNutritionFacts: NutritionFacts?,
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
    types: List<QuantityType>,
    selectedType: QuantityType,
    formField: FormField,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    onSelectQuantityType: (QuantityType) -> Unit,
    onNavigateToIngredient: (FoodSnapshotId, Quantity) -> Unit,
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
                    FavoriteIconButton(isFavorite = isFavorite, onChange = onSetFavorite)
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
            if (suggestions.isNotEmpty())
                item {
                    QuantitySuggestions(
                        suggestions = suggestions,
                        selectedType = selectedType,
                        formField = formField,
                        packageQuantity = packageQuantity,
                        servingQuantity = servingQuantity,
                        onSelectQuantity = onSelectQuantity,
                        modifier = Modifier.height(32.dp),
                    )
                }
            item {
                QuantityInput(
                    selectedType = selectedType,
                    types = types,
                    formField = formField,
                    onSelectType = onSelectQuantityType,
                    modifier = Modifier.padding(horizontal = 8.dp).focusRequester(focusRequester),
                )
            }
            if (scaledNutritionFacts != null)
                item {
                    Column {
                        FoodDetailsNutrientsCompact(
                            scaledNutritionFacts = scaledNutritionFacts,
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
                    FoodNote(note, Modifier.padding(horizontal = 8.dp))
                }
        }
    }
}
