package com.maksimowiczm.foodyou.features.food.ingredient

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
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.amount
import com.maksimowiczm.foodyou.features.food.common.FavoriteIconButton
import com.maksimowiczm.foodyou.features.food.common.FetchProgressIndicator
import com.maksimowiczm.foodyou.features.food.common.FoodDetailsNutrientsCompact
import com.maksimowiczm.foodyou.features.food.common.FoodScreenTopBar
import com.maksimowiczm.foodyou.features.food.common.FoodUiScope
import com.maksimowiczm.foodyou.features.food.common.FoodUiScopeWithFetch
import com.maksimowiczm.foodyou.features.food.common.FoodUiScopeWithImage
import com.maksimowiczm.foodyou.features.food.common.FoodUiScopeWithQuantityInput
import com.maksimowiczm.foodyou.features.food.common.FoodUiScopeWithSource
import com.maksimowiczm.foodyou.features.food.common.Headline
import com.maksimowiczm.foodyou.features.food.common.Image
import com.maksimowiczm.foodyou.features.food.common.QuantityInput
import com.maksimowiczm.foodyou.features.food.common.QuantitySuggestions
import com.maksimowiczm.foodyou.features.food.common.RefreshIconButton
import com.maksimowiczm.foodyou.features.food.common.SourceLink
import com.maksimowiczm.foodyou.features.food.common.rememberNutrientExpanded
import com.maksimowiczm.foodyou.features.food.common.rememberQuantityFormField
import com.maksimowiczm.foodyou.features.food.details.openfoodfacts.OpenFoodFactsDetailsUiState
import com.maksimowiczm.foodyou.features.food.details.openfoodfacts.OpenFoodFactsDetailsViewModel
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddOpenFoodFactsIngredientScreen(
    onBack: () -> Unit,
    onAdd: (Quantity) -> Unit,
    identity: OpenFoodFactsProductIdentity,
    initialQuantity: Quantity,
    modifier: Modifier = Modifier,
) {
    val viewModel: OpenFoodFactsDetailsViewModel = koinViewModel {
        parametersOf(identity, initialQuantity)
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val nameSelector = LocalFoodNameSelector.current

    val defaultValue = remember(initialQuantity) { initialQuantity.amount.formatCompact() }
    val formField = rememberQuantityFormField(defaultValue, defaultValue = defaultValue)

    val scope =
        remember(
            uiState,
            nameSelector,
            formField,
        ) {
            val details = uiState as? OpenFoodFactsDetailsUiState.Details ?: return@remember null
            val food = details.food

            AddIngredientOpenFoodFactsScope(
                headline = food.headline(nameSelector),
                isFavorite = details.isFavorite,
                isLoading = details.isLoading,
                image = food.image,
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

    scope?.Screen(
        onBack = onBack,
        onAdd = {
            scope.selectedQuantity?.let { onAdd(it) }
        },
        onRefresh = viewModel::refresh,
        onSetFavorite = viewModel::setFavorite,
        onSelectQuantity = {
            viewModel.selectQuantity(it)
            formField.textFieldState.setTextAndPlaceCursorAtEnd(it.amount.formatCompact())
        },
        onSelectQuantityType = viewModel::selectQuantityType,
        modifier = modifier,
    )
}

@Immutable
private data class AddIngredientOpenFoodFactsScope(
    override val headline: String?,
    override val isFavorite: Boolean,
    override val isLoading: Boolean,
    override val image: FileUri?,
    override val sourceUrl: String,
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
    FoodUiScopeWithFetch,
    FoodUiScopeWithImage,
    FoodUiScopeWithSource,
    FoodUiScopeWithQuantityInput

@Composable
private fun AddIngredientOpenFoodFactsScope.Screen(
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onAdd: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    onSelectQuantityType: (QuantityType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberNutrientExpanded()
    val expandingEnabled =
        remember(scaledNutritionFacts) {
            val scaledNutritionFacts = scaledNutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { scaledNutritionFacts[it].value != null }
        }

    val focusRequester = remember { FocusRequester() }
    var focusRequested by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!focusRequested) {
            val _ = runCatching { focusRequester.requestFocus() }
            focusRequested = true
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FoodScreenTopBar(
                onBack = onBack,
                title = headline,
                actions = {
                    FavoriteIconButton(favorite = isFavorite, onChange = onSetFavorite)
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
                            !isLoading &&
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
        FetchProgressIndicator(
            Modifier.padding(top = contentPadding.calculateTopPadding()).zIndex(100f)
        )
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
                    FoodDetailsNutrientsCompact(
                        expanded = if (!expandingEnabled) false else expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                }
            item {
                SourceLink(
                    logo = {
                        Image(
                            painter = painterResource(Res.drawable.openfoodfacts_logo),
                            contentDescription = null,
                            modifier =
                                Modifier.sizeIn(
                                    maxHeight = 32.dp,
                                    maxWidth = 32.dp,
                                ),
                        )
                    },
                    headline = stringResource(Res.string.headline_open_food_facts),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
            }
        }
    }
}
