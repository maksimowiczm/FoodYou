package com.maksimowiczm.foodyou.features.food.details.openfoodfacts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.capabilities.food.FetchProgressIndicator
import com.maksimowiczm.foodyou.capabilities.food.FoodDetailsNutrientsWithSuggestions
import com.maksimowiczm.foodyou.capabilities.food.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScope
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithFetch
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithImage
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithOptionalNutrients
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithSource
import com.maksimowiczm.foodyou.capabilities.food.Headline
import com.maksimowiczm.foodyou.capabilities.food.Image
import com.maksimowiczm.foodyou.capabilities.food.SourceLink
import com.maksimowiczm.foodyou.capabilities.food.rememberNutrientExpanded
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.component.RefreshIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun OpenFoodFactsDetailsScreen(
    identity: OpenFoodFactsProductIdentity,
    initialQuantity: Quantity?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: OpenFoodFactsDetailsViewModel = koinViewModel {
        parametersOf(identity, initialQuantity)
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val nameSelector = LocalFoodNameSelector.current

    val scope =
        remember(uiState, nameSelector) {
            val details = uiState as? OpenFoodFactsDetailsUiState.Details ?: return@remember null
            val food = details.food

            OpenFoodFactsScope(
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
            )
        }

    scope?.Screen(
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onSetFavorite = viewModel::setFavorite,
        onSelectQuantity = viewModel::selectQuantity,
        modifier = modifier,
    )
}

@Immutable
private data class OpenFoodFactsScope(
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
) :
    FoodUiScope,
    FoodUiScopeWithFetch,
    FoodUiScopeWithImage,
    FoodUiScopeWithSource,
    FoodUiScopeWithOptionalNutrients

@Composable
private fun OpenFoodFactsScope.Screen(
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var expanded by rememberNutrientExpanded()
    val expandingEnabled =
        remember(scaledNutritionFacts) {
            val scaledNutritionFacts = scaledNutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { scaledNutritionFacts[it].value != null }
        }

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
    ) { contentPadding ->
        FetchProgressIndicator(
            Modifier.padding(top = contentPadding.calculateTopPadding()).zIndex(100f)
        )
        LazyColumn(
            contentPadding = contentPadding.add(top = 26.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Headline(Modifier.padding(horizontal = 8.dp))
            }
            if (image != null)
                item {
                    Image(Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            if (scaledNutritionFacts != null)
                item {
                    FoodDetailsNutrientsWithSuggestions(
                        onSelectQuantity = onSelectQuantity,
                        expanded = if (!expandingEnabled) false else expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        contentPadding = PaddingValues(horizontal = 8.dp),
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
