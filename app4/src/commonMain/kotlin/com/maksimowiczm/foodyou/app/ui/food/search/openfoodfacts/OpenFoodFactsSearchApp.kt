package com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts

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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.error
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchErrorCard
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.fold
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun OpenFoodFactsSearchApp(
    shimmer: Shimmer,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    onClick: (OpenFoodFactsProduct) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OpenFoodFactsSearchViewModel = koinViewModel(),
) {
    val density = LocalDensity.current
    val pages = viewModel.pages.collectAsLazyPagingItems()

    val error = pages.loadState.error
    var errorCardHeight by remember { mutableIntStateOf(0) }

    Box(modifier) {
        LazyColumn(
            state = lazyListState,
            contentPadding = contentPadding.add(top = density.run { errorCardHeight.toDp() }),
        ) {
            items(count = pages.itemCount, key = pages.itemKey { it.identity.barcode }) { i ->
                when (val food = pages[i]) {
                    null -> FoodListItemSkeleton(shimmer)
                    else ->
                        FoodSearchListItem(
                            food = food,
                            onClick = { onClick(food) },
                            shimmer = shimmer,
                        )
                }
            }

            if (pages.loadState.append is LoadState.Loading) {
                items(10) { FoodListItemSkeleton(shimmer) }
            }
        }

        Box(
            Modifier.padding(top = contentPadding.calculateTopPadding()).onSizeChanged {
                errorCardHeight = it.height
            }
        ) {
            when (error) {
                is OpenFoodFactsApiError.RateLimitExceeded ->
                    FoodSearchErrorCard(
                        message = stringResource(Res.string.error_open_food_facts_rate_limit),
                        onRetry = pages::retry,
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                    )

                is Throwable ->
                    FoodSearchErrorCard(
                        message = error.message,
                        onRetry = pages::retry,
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                    )
            }
        }

        if (pages.itemCount == 0 && pages.loadState.isIdle) {
            Text(
                text = stringResource(Res.string.neutral_no_food_found),
                modifier = Modifier.safeContentPadding().align(Alignment.Center),
            )
        } else if (!pages.loadState.isIdle && error == null) {
            ContainedLoadingIndicator(
                Modifier.align(Alignment.TopCenter)
                    .padding(top = contentPadding.calculateTopPadding())
            )
        }
    }
}

@Composable
private fun FoodSearchListItem(
    food: OpenFoodFactsProduct,
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

    val absoluteQuantity = food.packageQuantity ?: AbsoluteQuantity.Weight(Grams(100.0))

    val measurementFacts =
        remember(food, absoluteQuantity) {
            val factor =
                when (absoluteQuantity) {
                    is AbsoluteQuantity.Volume -> absoluteQuantity.volume.milliliters / 100.0
                    is AbsoluteQuantity.Weight -> absoluteQuantity.weight.grams / 100.0
                }

            food.nutritionFacts * factor
        }

    val measurementString =
        absoluteQuantity
            .stringResource(food.packageQuantity, food.servingQuantity)
            .fold(onSuccess = { it }, onError = { absoluteQuantity.stringResource() })

    FoodSearchListItem(
        headline = nameSelector.select(food.name),
        proteins = measurementFacts.proteins.value,
        carbohydrates = measurementFacts.carbohydrates.value,
        fats = measurementFacts.fats.value,
        energy = measurementFacts.energy.value,
        quantity = { Text(measurementString) },
        image = food.image?.let { @Composable { it.Image(shimmer, Modifier.size(56.dp)) } },
        onClick = onClick,
        modifier = modifier,
    )
}
