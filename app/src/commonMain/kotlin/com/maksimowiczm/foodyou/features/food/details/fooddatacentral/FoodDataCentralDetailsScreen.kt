package com.maksimowiczm.foodyou.features.food.details.fooddatacentral

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
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.features.food.common.FavoriteIconButton
import com.maksimowiczm.foodyou.features.food.common.FetchProgressIndicator
import com.maksimowiczm.foodyou.features.food.common.FoodDetailsNutrientsWithSuggestions
import com.maksimowiczm.foodyou.features.food.common.FoodScreenTopBar
import com.maksimowiczm.foodyou.features.food.common.FoodUiScope
import com.maksimowiczm.foodyou.features.food.common.FoodUiScopeWithFetch
import com.maksimowiczm.foodyou.features.food.common.FoodUiScopeWithOptionalNutrients
import com.maksimowiczm.foodyou.features.food.common.FoodUiScopeWithSource
import com.maksimowiczm.foodyou.features.food.common.Headline
import com.maksimowiczm.foodyou.features.food.common.RefreshIconButton
import com.maksimowiczm.foodyou.features.food.common.SourceLink
import com.maksimowiczm.foodyou.features.food.common.rememberNutrientExpanded
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.shared.ui.extension.add
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FoodDataCentralDetailsScreen(
    identity: FoodDataCentralProductIdentity,
    initialQuantity: Quantity?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: FoodDataCentralDetailsViewModel = koinViewModel {
        parametersOf(identity, initialQuantity)
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val scope =
        remember(uiState) {
            val details = uiState as? FoodDataCentralDetailsUiState.Details ?: return@remember null
            val food = details.food

            FoodDataCentralScope(
                headline = food.headline,
                isFavorite = details.isFavorite,
                isLoading = details.isLoading,
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
private data class FoodDataCentralScope(
    override val headline: String?,
    override val isFavorite: Boolean,
    override val isLoading: Boolean,
    override val sourceUrl: String,
    override val suggestions: List<Quantity>,
    override val selectedQuantity: Quantity?,
    override val scaledNutritionFacts: NutritionFacts?,
    override val packageQuantity: AbsoluteQuantity?,
    override val servingQuantity: AbsoluteQuantity?,
) : FoodUiScope, FoodUiScopeWithFetch, FoodUiScopeWithSource, FoodUiScopeWithOptionalNutrients

@Composable
private fun FoodDataCentralScope.Screen(
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
            item { Headline(Modifier.padding(horizontal = 8.dp)) }
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
