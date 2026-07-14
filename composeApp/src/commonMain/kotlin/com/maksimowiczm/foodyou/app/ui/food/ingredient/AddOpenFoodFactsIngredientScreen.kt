package com.maksimowiczm.foodyou.app.ui.food.ingredient

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.maksimowiczm.foodyou.app.ui.common.component.QuantityInput
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.headline
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsHeadline
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsImage
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsLoadingOverlay
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsTopBar
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsUiState
import com.maksimowiczm.foodyou.app.ui.food.details.FoodSource
import com.maksimowiczm.foodyou.app.ui.food.details.FoodSourceDefaults
import com.maksimowiczm.foodyou.app.ui.food.details.RefreshIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.openfoodfacts.OpenFoodFactsDetailsViewModel
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.scale
import com.maksimowiczm.foodyou.common.getOrNull
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
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
    val viewModel: OpenFoodFactsDetailsViewModel =
        koinViewModel(parameters = { parametersOf(identity) })

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val nameSelector = LocalFoodNameSelector.current
    val headline =
        remember(uiState, nameSelector) {
            when (uiState) {
                is FoodDetailsUiState.Error -> null
                FoodDetailsUiState.NotFound -> null
                is FoodDetailsUiState.Details<OpenFoodFactsProduct> ->
                    uiState.food?.headline(nameSelector)
            }
        }

    when (uiState) {
        is FoodDetailsUiState.Details<OpenFoodFactsProduct> -> {
            val quantityState =
                rememberAddIngredientQuantityState(
                    initialQuantity = initialQuantity,
                    servingQuantity = uiState.food?.servingQuantity,
                    packageQuantity = uiState.food?.packageQuantity,
                    isLiquid = false, // TODO
                )

            AddOpenFoodFactsIngredientScreen(
                isLoading = uiState.isLoading,
                onBack = onBack,
                onAdd = { onAdd(quantityState.quantity) },
                isFavorite = uiState.isFavorite,
                onSetFavorite = viewModel::setFavorite,
                onRefresh = viewModel::refresh,
                headline = headline,
                image = uiState.food?.image,
                nutritionFacts = uiState.food?.nutritionFacts,
                servingQuantity = uiState.food?.servingQuantity,
                packageQuantity = uiState.food?.packageQuantity,
                url = uiState.food?.source,
                modifier = modifier,
                quantityState = quantityState,
            )
        }

        is FoodDetailsUiState.Error,
        FoodDetailsUiState.NotFound -> Unit
    }
}

@Composable
private fun AddOpenFoodFactsIngredientScreen(
    isLoading: Boolean,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    isFavorite: Boolean,
    onSetFavorite: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    headline: String?,
    image: FileUri?,
    nutritionFacts: NutritionFacts?,
    servingQuantity: AbsoluteQuantity?,
    packageQuantity: AbsoluteQuantity?,
    url: String?,
    quantityState: AddIngredientQuantityState,
    modifier: Modifier = Modifier,
) {
    val expanded = rememberNutrientExpanded()
    val expandingEnabled =
        remember(nutritionFacts) {
            if (nutritionFacts == null) return@remember false
            (Nutrient.all - Nutrient.basic).any { nutritionFacts[it].value != null }
        }

    val scaledNutritionFacts =
        remember(nutritionFacts, quantityState.quantity) {
            nutritionFacts
                ?.scale(packageQuantity, servingQuantity, quantityState.quantity)
                ?.getOrNull()
        }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodDetailsTopBar(
                onBack = onBack,
                title = headline,
                scrollBehavior = scrollBehavior,
                actions = {
                    FavoriteIconButton(favorite = isFavorite, onChange = onSetFavorite)
                    RefreshIconButton(onRefresh = onRefresh)
                },
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
                                quantityState.formField.error == null,
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
        Box(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
            FoodDetailsLoadingOverlay(
                isLoading = isLoading,
                topPadding = contentPadding.calculateTopPadding(),
            )
            LazyColumn(contentPadding = contentPadding.add(top = 26.dp, bottom = 128.dp)) {
                item { FoodDetailsHeadline(headline = headline) }
                item { Spacer(Modifier.height(16.dp)) }
                item { FoodDetailsImage(image = image, showPlaceholder = isLoading) }
                item { Spacer(Modifier.height(16.dp)) }
                item {
                    QuantityInput(
                        entries = quantityState.quantityTypes,
                        selectedQuantity = quantityState.selectedQuantityType,
                        onQuantity = quantityState::onSelectedQuantityTypeChange,
                        formField = quantityState.formField,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
                if (scaledNutritionFacts != null) {
                    item {
                        AddIngredientNutrients(
                            nutritionFacts = scaledNutritionFacts,
                            quantities = quantityState.quantitySuggestions,
                            servingQuantity = servingQuantity,
                            packageQuantity = packageQuantity,
                            onSelectQuantity = quantityState::onSelectQuantity,
                            expanded = expanded.value,
                            onExpandedChange = { expanded.value = it },
                            expandingEnabled = expandingEnabled,
                        )
                    }
                }
                if (url != null) {
                    item { Spacer(Modifier.height(16.dp)) }
                    item {
                        FoodSource(
                            url = url,
                            logo = {
                                Image(
                                    painter = painterResource(Res.drawable.openfoodfacts_logo),
                                    contentDescription = null,
                                    modifier =
                                        Modifier.sizeIn(
                                            maxHeight = FoodSourceDefaults.logoMaxSize,
                                            maxWidth = FoodSourceDefaults.logoMaxSize,
                                        ),
                                )
                            },
                            headline = stringResource(Res.string.headline_open_food_facts),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}
