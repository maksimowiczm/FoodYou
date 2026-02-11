package com.maksimowiczm.foodyou.app.ui.food.search.favoritefood

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.extension.debounceIsIdle
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.RemoteData
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.fold
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.userfood.domain.product.UserFoodProduct
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun FavoriteFoodSearchApp(
    shimmer: Shimmer,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    onClick: (Any) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteFoodSearchViewModel = koinViewModel(),
) {
    val pages = viewModel.pages.collectAsLazyPagingItems()
    val isLoading =
        remember(pages) { pages.debounceIsIdle().map { !it } }
            .collectAsStateWithLifecycle(false)
            .value

    Box(modifier) {
        LazyColumn(state = lazyListState, contentPadding = contentPadding) {
            items(count = pages.itemCount) { i ->
                when (val food = pages[i]) {
                    null -> FoodListItemSkeleton(shimmer)
                    else -> {
                        val name = food.name()
                        val image = food.image()
                        val packageQuantity = food.packageQuantity()
                        val servingQuantity = food.servingQuantity()
                        val nutritionFacts = food.nutritionFacts()

                        if (name == null || nutritionFacts == null) {
                            FoodListItemSkeleton(shimmer)
                        } else {
                            FoodSearchListItem(
                                name = name,
                                packageQuantity = packageQuantity,
                                servingQuantity = servingQuantity,
                                nutritionFacts = nutritionFacts,
                                image = image,
                                shimmer = shimmer,
                                onClick = {
                                    when (food) {
                                        is RemoteData.Success -> onClick(food.value)
                                        is RemoteData.Error ->
                                            food.partialValue?.let { onClick(it) }

                                        is RemoteData.Loading ->
                                            food.partialValue?.let { onClick(it) }

                                        is RemoteData.NotFound -> Unit
                                    }
                                },
                            )
                        }
                    }
                }
            }

            if (
                pages.loadState.append is LoadState.Loading ||
                    pages.loadState.refresh is LoadState.Loading
            ) {
                items(10) { FoodListItemSkeleton(shimmer) }
            }
        }

        if (pages.itemCount == 0 && !isLoading) {
            Text(
                text = stringResource(Res.string.neutral_no_food_found),
                modifier = Modifier.safeContentPadding().align(Alignment.Center),
            )
        }

        if (isLoading) {
            ContainedLoadingIndicator(
                Modifier.align(Alignment.TopCenter)
                    .padding(top = contentPadding.calculateTopPadding())
            )
        }
    }
}

@Composable
private fun FoodSearchListItem(
    name: FoodName,
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
    nutritionFacts: NutritionFacts,
    image: Image?,
    shimmer: Shimmer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

    val absoluteQuantity = packageQuantity ?: AbsoluteQuantity.Weight(Grams(100.0))

    val measurementFacts =
        remember(absoluteQuantity, nutritionFacts) {
            val factor =
                when (absoluteQuantity) {
                    is AbsoluteQuantity.Volume -> absoluteQuantity.volume.milliliters / 100.0
                    is AbsoluteQuantity.Weight -> absoluteQuantity.weight.grams / 100.0
                }

            nutritionFacts * factor
        }

    val measurementString =
        absoluteQuantity
            .stringResource(packageQuantity, servingQuantity)
            .fold(onSuccess = { it }, onError = { absoluteQuantity.stringResource() })

    FoodSearchListItem(
        headline = nameSelector.select(name),
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image = image?.let { @Composable { it.Image(shimmer, Modifier.size(56.dp)) } },
        onClick = onClick,
        modifier = modifier,
    )
}

private fun Any.name(): FoodName =
    when (this) {
        is UserFoodProduct -> name
        is OpenFoodFactsProduct -> name
        is FoodDataCentralProduct -> name
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.name(): FoodName? =
    when (this) {
        is RemoteData.Success -> value.name()
        is RemoteData.Error -> partialValue?.name()
        is RemoteData.Loading -> partialValue?.name()
        is RemoteData.NotFound -> null
    }

private fun Any.packageQuantity(): AbsoluteQuantity? =
    when (this) {
        is UserFoodProduct -> packageQuantity
        is OpenFoodFactsProduct -> packageQuantity
        is FoodDataCentralProduct -> packageQuantity
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.packageQuantity(): AbsoluteQuantity? =
    when (this) {
        is RemoteData.Success -> value.packageQuantity()
        is RemoteData.Error -> partialValue?.packageQuantity()
        is RemoteData.Loading -> partialValue?.packageQuantity()
        is RemoteData.NotFound -> null
    }

private fun Any.servingQuantity(): AbsoluteQuantity? =
    when (this) {
        is UserFoodProduct -> servingQuantity
        is OpenFoodFactsProduct -> servingQuantity
        is FoodDataCentralProduct -> servingQuantity
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.servingQuantity(): AbsoluteQuantity? =
    when (this) {
        is RemoteData.Success -> value.servingQuantity()
        is RemoteData.Error -> partialValue?.servingQuantity()
        is RemoteData.Loading -> partialValue?.servingQuantity()
        is RemoteData.NotFound -> null
    }

private fun Any.nutritionFacts(): NutritionFacts =
    when (this) {
        is UserFoodProduct -> nutritionFacts
        is OpenFoodFactsProduct -> nutritionFacts
        is FoodDataCentralProduct -> nutritionFacts
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.nutritionFacts(): NutritionFacts? =
    when (this) {
        is RemoteData.Success -> value.nutritionFacts()
        is RemoteData.Error -> partialValue?.nutritionFacts()
        is RemoteData.Loading -> partialValue?.nutritionFacts()
        is RemoteData.NotFound -> null
    }

private fun Any.image(): Image? =
    when (this) {
        is UserFoodProduct -> image
        is OpenFoodFactsProduct -> image
        is FoodDataCentralProduct -> null
        else -> error("Unknown type ${this::class}")
    }

private fun RemoteData<Any>.image(): Image? =
    when (this) {
        is RemoteData.Success -> value.image()
        is RemoteData.Error -> partialValue?.image()
        is RemoteData.Loading -> partialValue?.image()
        is RemoteData.NotFound -> null
    }
