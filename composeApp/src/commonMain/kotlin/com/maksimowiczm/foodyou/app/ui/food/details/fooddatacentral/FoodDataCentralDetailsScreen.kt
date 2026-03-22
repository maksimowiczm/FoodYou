package com.maksimowiczm.foodyou.app.ui.food.details.fooddatacentral

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtection
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsHeadline
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsLoadingOverlay
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsNutrients
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsTopBar
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsUiState
import com.maksimowiczm.foodyou.app.ui.food.details.FoodSource
import com.maksimowiczm.foodyou.app.ui.food.details.FoodSourceDefaults
import com.maksimowiczm.foodyou.app.ui.food.details.RefreshIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.rememberFoodDetailsChrome
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FoodDataCentralDetailsScreen(
    identity: FoodDataCentralProductIdentity,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: FoodDataCentralDetailsViewModel =
        koinViewModel(parameters = { parametersOf(identity) })

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val nameSelector = LocalFoodNameSelector.current
    val headline =
        remember(uiState, nameSelector) {
            when (uiState) {
                is FoodDetailsUiState.Error -> null
                FoodDetailsUiState.NotFound -> null
                is FoodDetailsUiState.Details<FoodDataCentralProduct> -> {
                    val name = uiState.food?.name
                    val brand = uiState.food?.brand
                    if (name == null) null
                    else
                        buildString {
                            append(nameSelector.select(name))
                            append(brand?.let { " ($it)" } ?: "")
                        }
                }
            }
        }

    when (uiState) {
        is FoodDetailsUiState.Details<FoodDataCentralProduct> ->
            FoodDataCentralDetailsScreen(
                isLoading = uiState.isLoading,
                isFavorite = uiState.isFavorite,
                headline = headline,
                nutritionFacts = uiState.food?.nutritionFacts,
                url = uiState.food?.source,
                onBack = onBack,
                onRefresh = viewModel::refresh,
                onSetFavorite = viewModel::setFavorite,
                modifier = modifier,
            )

        is FoodDetailsUiState.Error,
        FoodDetailsUiState.NotFound -> error("Not possible for now")
    }
}

@Composable
private fun FoodDataCentralDetailsScreen(
    isLoading: Boolean,
    isFavorite: Boolean,
    headline: String?,
    nutritionFacts: NutritionFacts?,
    url: String?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberNutrientExpanded()
    val expandingEnabled =
        remember(nutritionFacts) {
            if (nutritionFacts == null) return@remember false
            (Nutrient.all - Nutrient.basic).any { nutritionFacts[it].value != null }
        }

    val lazyListState = rememberLazyListState()
    val chrome = rememberFoodDetailsChrome(lazyListState)

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodDetailsTopBar(
                onBack = onBack,
                iconButtonContainerColor = chrome.iconButtonContainerColor,
                actions = {
                    FavoriteIconButton(
                        favorite = isFavorite,
                        onChange = onSetFavorite,
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = chrome.iconButtonContainerColor
                            ),
                    )
                    RefreshIconButton(
                        onRefresh = onRefresh,
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = chrome.iconButtonContainerColor
                            ),
                    )
                },
            )
        },
    ) { contentPadding ->
        Box {
            FoodDetailsLoadingOverlay(
                isLoading = isLoading,
                topPadding = contentPadding.calculateTopPadding(),
            )
            LazyColumn(
                state = lazyListState,
                contentPadding = contentPadding.add(top = 26.dp, bottom = 8.dp),
            ) {
                item { FoodDetailsHeadline(headline = headline) }
                if (nutritionFacts != null) {
                    item {
                        FoodDetailsNutrients(
                            nutritionFacts = nutritionFacts,
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            expandingEnabled = expandingEnabled,
                        )
                    }
                }
                if (url != null) {
                    item {
                        HorizontalDivider(Modifier.fillMaxWidth().padding(16.dp))
                        FoodSource(
                            url = url,
                            logo = {
                                Image(
                                    painter = painterResource(Res.drawable.usda_logo),
                                    contentDescription = null,
                                    modifier =
                                        Modifier.sizeIn(
                                            maxHeight = FoodSourceDefaults.logoMaxSize,
                                            maxWidth = FoodSourceDefaults.logoMaxSize,
                                        ),
                                )
                            },
                            headline = stringResource(Res.string.headline_food_data_central_usda),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
    StatusBarProtection(MaterialTheme.colorScheme.surfaceContainerHigh) {
        chrome.statusBarProtection
    }
}
