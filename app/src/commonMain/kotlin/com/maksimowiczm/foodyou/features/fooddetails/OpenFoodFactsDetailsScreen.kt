package com.maksimowiczm.foodyou.features.fooddetails

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
import com.maksimowiczm.foodyou.capabilities.fooddetails.FetchProgressIndicator
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsWithSuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodImage
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodSourceLink
import com.maksimowiczm.foodyou.capabilities.fooddetails.openfoodfacts.OpenFoodFactsDetailsUiState
import com.maksimowiczm.foodyou.capabilities.fooddetails.openfoodfacts.OpenFoodFactsDetailsViewModel
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientsExpanded
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductId
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
    id: OpenFoodFactsProductId,
    initialQuantity: Quantity?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: OpenFoodFactsDetailsViewModel = koinViewModel {
        parametersOf(id, initialQuantity)
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val nameSelector = LocalFoodNameSelector.current

    val details = uiState as? OpenFoodFactsDetailsUiState.Details

    if (details != null) {
        OpenFoodFactsDetailsScreenContent(
            headline = details.food.headline(nameSelector),
            isFavorite = details.isFavorite,
            isLoading = details.isLoading,
            image = details.food.image,
            sourceUrl = details.food.source,
            suggestions = details.suggestions,
            selectedQuantity = details.selectedQuantity,
            scaledNutritionFacts = details.scaledNutritionFacts,
            packageQuantity = details.food.packageQuantity,
            servingQuantity = details.food.servingQuantity,
            onBack = onBack,
            onRefresh = viewModel::refresh,
            onSetFavorite = viewModel::setFavorite,
            onSelectQuantity = viewModel::selectQuantity,
            modifier = modifier,
        )
    }
}

@Composable
private fun OpenFoodFactsDetailsScreenContent(
    headline: String?,
    isFavorite: Boolean,
    isLoading: Boolean,
    image: FileUri?,
    sourceUrl: String,
    suggestions: List<Quantity>,
    selectedQuantity: Quantity?,
    scaledNutritionFacts: NutritionFacts?,
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
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
                    RefreshIconButton(onRefresh = onRefresh)
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        FetchProgressIndicator(
            isLoading = isLoading,
            modifier = Modifier.padding(top = contentPadding.calculateTopPadding()).zIndex(100f),
        )
        LazyColumn(
            contentPadding = contentPadding.add(top = 26.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                FoodHeadline(headline, Modifier.padding(horizontal = 8.dp))
            }
            if (image != null)
                item {
                    FoodImage(image, Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            if (scaledNutritionFacts != null)
                item {
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
                }
            item {
                FoodSourceLink(
                    sourceUrl = sourceUrl,
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
