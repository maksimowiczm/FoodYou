package com.maksimowiczm.foodyou.features.userrecipe.ingredient

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.maksimowiczm.foodyou.capabilities.fooddetails.FetchProgressIndicator
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsCompact
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodSourceLink
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantityInput
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantitySuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.fooddatacentral.FoodDataCentralDetailsUiState
import com.maksimowiczm.foodyou.capabilities.fooddetails.fooddatacentral.FoodDataCentralDetailsViewModel
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientExpanded
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberQuantityFormField
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.amount
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.component.RefreshIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddFoodDataCentralIngredientScreen(
    onBack: () -> Unit,
    onAdd: (Quantity) -> Unit,
    identity: FoodDataCentralProductIdentity,
    initialQuantity: Quantity,
    modifier: Modifier = Modifier,
) {
    val viewModel: FoodDataCentralDetailsViewModel = koinViewModel {
        parametersOf(identity, initialQuantity)
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val defaultValue = remember(initialQuantity) { initialQuantity.amount.formatCompact() }

    val formField = rememberQuantityFormField(defaultValue, defaultValue = defaultValue)

    val scope =
        remember(
            uiState,
            formField,
        ) {
            val details = uiState as? FoodDataCentralDetailsUiState.Details ?: return@remember null
            val food = details.food

            AddIngredientFoodDataCentralScope(
                headline = food.headline,
                isFavorite = details.isFavorite,
                isLoading = details.isLoading,
                sourceUrl = food.source,
                suggestions = details.suggestions,
                selectedQuantity = details.selectedQuantity,
                scaledNutritionFacts = details.scaledNutritionFacts,
                packageQuantity = food.packageQuantity,
                servingQuantity = food.servingQuantity,
                types = details.quantityTypes,
                selectedType = details.selectedQuantityType,
                formField = formField,
            )
        }

    LaunchedEffect(formField.textFieldState.text, scope?.selectedType) {
        viewModel.selectQuantity(
            formField.textFieldState.text.toString().toDoubleOrNull(),
            scope?.selectedType,
        )
    }

    scope?.let {
        AddFoodDataCentralIngredientScreenContent(
            scope = it,
            onBack = onBack,
            onAdd = {
                onAdd(it.selectedQuantity ?: return@AddFoodDataCentralIngredientScreenContent)
            },
            onRefresh = viewModel::refresh,
            onSetFavorite = viewModel::setFavorite,
            onSelectQuantity = { quantity ->
                viewModel.selectQuantity(quantity)
                formField.textFieldState.setTextAndPlaceCursorAtEnd(quantity.amount.formatCompact())
            },
            onSelectQuantityType = viewModel::selectQuantityType,
            modifier = modifier,
        )
    }
}

@Immutable
private data class AddIngredientFoodDataCentralScope(
    val headline: String?,
    val isFavorite: Boolean,
    val isLoading: Boolean,
    val sourceUrl: String,
    val suggestions: List<Quantity>,
    val selectedQuantity: Quantity?,
    val scaledNutritionFacts: NutritionFacts?,
    val packageQuantity: AbsoluteQuantity?,
    val servingQuantity: AbsoluteQuantity?,
    val types: List<QuantityType>,
    val selectedType: QuantityType,
    val formField: FormField,
)

@Composable
private fun AddFoodDataCentralIngredientScreenContent(
    scope: AddIngredientFoodDataCentralScope,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onRefresh: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    onSelectQuantityType: (QuantityType) -> Unit,
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
        remember(scope.scaledNutritionFacts) {
            val scaledNutritionFacts = scope.scaledNutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { scaledNutritionFacts[it].value != null }
        }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FoodScreenTopBar(
                onBack = onBack,
                title = scope.headline,
                actions = {
                    FavoriteIconButton(isFavorite = scope.isFavorite, onChange = onSetFavorite)
                    RefreshIconButton(onRefresh = onRefresh)
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
                            !scope.isLoading &&
                                !LocalNavAnimatedContentScope.current.transition.isRunning &&
                                scope.formField.error == null,
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
        FetchProgressIndicator(
            isLoading = scope.isLoading,
            modifier = Modifier.padding(top = contentPadding.calculateTopPadding()).zIndex(100f),
        )
        LazyColumn(
            modifier = Modifier.imePadding(),
            contentPadding = contentPadding.add(top = 26.dp, bottom = 128.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { FoodHeadline(scope.headline, Modifier.padding(horizontal = 8.dp)) }
            if (scope.suggestions.isNotEmpty())
                item {
                    QuantitySuggestions(
                        suggestions = scope.suggestions,
                        selectedType = scope.selectedType,
                        formField = scope.formField,
                        packageQuantity = scope.packageQuantity,
                        servingQuantity = scope.servingQuantity,
                        onSelectQuantity = onSelectQuantity,
                        modifier = Modifier.height(32.dp),
                    )
                }
            item {
                QuantityInput(
                    selectedType = scope.selectedType,
                    types = scope.types,
                    formField = scope.formField,
                    onSelectType = onSelectQuantityType,
                    modifier = Modifier.padding(horizontal = 8.dp).focusRequester(focusRequester),
                )
            }
            if (scope.scaledNutritionFacts != null)
                item {
                    FoodDetailsNutrientsCompact(
                        scaledNutritionFacts = scope.scaledNutritionFacts,
                        expanded = if (!expandingEnabled) false else expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                }
            item {
                FoodSourceLink(
                    sourceUrl = scope.sourceUrl,
                    logo = {
                        Image(
                            painter = painterResource(Res.drawable.usda_logo),
                            contentDescription = null,
                            modifier =
                                Modifier.sizeIn(
                                    maxHeight = 32.dp,
                                    maxWidth = 32.dp,
                                ),
                        )
                    },
                    headline = stringResource(Res.string.headline_fooddata_central),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
            }
        }
    }
}
