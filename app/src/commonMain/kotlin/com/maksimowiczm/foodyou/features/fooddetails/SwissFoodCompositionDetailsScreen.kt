package com.maksimowiczm.foodyou.features.fooddetails

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsWithSuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodSourceLink
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientsExpanded
import com.maksimowiczm.foodyou.capabilities.fooddetails.swissfoodcomposition.SwissFoodCompositionDetailsUiState
import com.maksimowiczm.foodyou.capabilities.fooddetails.swissfoodcomposition.SwissFoodCompositionDetailsViewModel
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreen
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionProductId
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.swissFoodCompositionDatabaseUrl
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SwissFoodCompositionDetailsScreen(
    id: SwissFoodCompositionProductId,
    initialQuantity: Quantity?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SwissFoodCompositionDetailsViewModel = koinViewModel {
        parametersOf(id, initialQuantity)
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    updateTransition(uiState).Crossfade(modifier) { details ->
        when (details) {
            is SwissFoodCompositionDetailsUiState.Details ->
                SwissFoodCompositionDetailsScreenContent(
                    headline = LocalFoodNameSelector.current.select(details.food.name),
                    isFavorite = details.isFavorite,
                    suggestions = details.suggestions,
                    selectedQuantity = details.selectedQuantity,
                    scaledNutritionFacts = details.scaledNutritionFacts,
                    onBack = onBack,
                    onSetFavorite = viewModel::setFavorite,
                    onSelectQuantity = viewModel::selectQuantity,
                    modifier = modifier,
                )

            SwissFoodCompositionDetailsUiState.Loading,
            SwissFoodCompositionDetailsUiState.NotFound -> LoadingScreen(onBack)
        }
    }
}

@Composable
private fun SwissFoodCompositionDetailsScreenContent(
    headline: String?,
    isFavorite: Boolean,
    suggestions: List<Quantity>,
    selectedQuantity: Quantity?,
    scaledNutritionFacts: NutritionFacts?,
    onBack: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var expanded by rememberNutrientsExpanded()
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
                    FavoriteIconButton(isFavorite = isFavorite, onChange = onSetFavorite)
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding.add(top = 26.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { FoodHeadline(headline, Modifier.padding(horizontal = 16.dp)) }
            if (scaledNutritionFacts != null)
                item {
                    FoodDetailsNutrientsWithSuggestions(
                        scaledNutritionFacts = scaledNutritionFacts,
                        suggestions = suggestions,
                        selectedQuantity = selectedQuantity,
                        packageQuantity = null,
                        servingQuantity = null,
                        onSelectQuantity = onSelectQuantity,
                        expanded = expandingEnabled && expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    )
                }
            item {
                FoodSourceLink(
                    sourceUrl = swissFoodCompositionDatabaseUrl,
                    headline = stringResource(Res.string.headline_swiss_food_composition),
                    logo = {},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
            }
        }
    }
}
