package com.maksimowiczm.foodyou.app.ui.food.details.fooddatacentral

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsHeadline
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsLoadingOverlay
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsNutrients
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsTopBar
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsUiState
import com.maksimowiczm.foodyou.app.ui.food.details.FoodSource
import com.maksimowiczm.foodyou.app.ui.food.details.FoodSourceDefaults
import com.maksimowiczm.foodyou.app.ui.food.details.RefreshIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.food.scale
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.getOrNull
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
    initialQuantity: Quantity?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: FoodDataCentralDetailsViewModel =
        koinViewModel(parameters = { parametersOf(identity) })

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val headline =
        remember(uiState) {
            when (uiState) {
                is FoodDetailsUiState.Error -> null
                FoodDetailsUiState.NotFound -> null
                is FoodDetailsUiState.Details<FoodDataCentralProduct> -> uiState.food?.headline
            }
        }

    when (uiState) {
        is FoodDetailsUiState.Details<FoodDataCentralProduct> ->
            FoodDataCentralDetailsScreen(
                isLoading = uiState.isLoading,
                isFavorite = uiState.isFavorite,
                headline = headline,
                initialQuantity = initialQuantity,
                nutritionFacts = uiState.food?.nutritionFacts,
                servingQuantity = uiState.food?.servingQuantity,
                packageQuantity = uiState.food?.packageQuantity,
                isLiquid = false, // TODO
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
    initialQuantity: Quantity?,
    nutritionFacts: NutritionFacts?,
    servingQuantity: AbsoluteQuantity?,
    packageQuantity: AbsoluteQuantity?,
    isLiquid: Boolean,
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

    var quantity by
        rememberSerializable(initialQuantity, servingQuantity, packageQuantity, isLiquid) {
            val default =
                initialQuantity
                    ?: if (servingQuantity != null) ServingQuantity(1.0)
                    else if (packageQuantity != null) PackageQuantity(1.0)
                    else if (isLiquid) AbsoluteQuantity.Volume(100.milliliters)
                    else AbsoluteQuantity.Weight(100.grams)

            mutableStateOf(default)
        }
    val quantitySuggestions =
        remember(initialQuantity, servingQuantity, packageQuantity, isLiquid) {
            buildList {
                if (isLiquid) add(AbsoluteQuantity.Volume(100.milliliters))
                else add(AbsoluteQuantity.Weight(100.grams))
                if (initialQuantity != null) add(initialQuantity)
                if (servingQuantity != null) add(ServingQuantity(1.0))
                if (packageQuantity != null) add(PackageQuantity(1.0))
            }
                .distinct()
        }

    val scaledNutritionFacts =
        remember(nutritionFacts, quantity) {
            nutritionFacts?.scale(packageQuantity, servingQuantity, quantity)?.getOrNull()
        }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodDetailsTopBar(
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
        Box(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
            FoodDetailsLoadingOverlay(
                isLoading = isLoading,
                modifier =
                    Modifier.padding(top = contentPadding.calculateTopPadding()).zIndex(100f),
            )
            LazyColumn(
                contentPadding = contentPadding.add(top = 26.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    FoodDetailsHeadline(
                        headline = headline,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                }
                if (scaledNutritionFacts != null) {
                    item {
                        FoodDetailsNutrients(
                            nutritionFacts = scaledNutritionFacts,
                            quantities = quantitySuggestions,
                            selectedQuantity = quantity,
                            servingQuantity = servingQuantity,
                            packageQuantity = packageQuantity,
                            onSelectQuantity = { quantity = it },
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            expandingEnabled = expandingEnabled,
                        )
                    }
                }
                if (url != null) {
                    item {
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
                            headline = stringResource(Res.string.headline_fooddata_central),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        )
                    }
                }
            }
        }
    }
}
