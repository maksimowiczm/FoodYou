package com.maksimowiczm.foodyou.app.ui.food.search.userfood

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
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.app.ui.common.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.extension.debounceIsIdle
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.search.FoodSearchListItem
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Grams
import com.maksimowiczm.foodyou.common.domain.food.Milliliters
import com.maksimowiczm.foodyou.common.fold
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProduct
import com.valentinilk.shimmer.Shimmer
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UserFoodSearchApp(
    shimmer: Shimmer,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    onClick: (UserFoodProduct) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserFoodSearchViewModel = koinViewModel(),
) {
    val pages = viewModel.pages.collectAsLazyPagingItems()
    val isLoading =
        remember(pages) { pages.debounceIsIdle().map { !it } }
            .collectAsStateWithLifecycle(false)
            .value

    Box(modifier) {
        LazyColumn(state = lazyListState, contentPadding = contentPadding) {
            items(count = pages.itemCount, key = pages.itemKey { it.identity.toString() }) { i ->
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
    food: UserFoodProduct,
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

    val absoluteQuantity =
        when (food.isLiquid) {
            false -> AbsoluteQuantity.Weight(Grams(100.0))
            true -> AbsoluteQuantity.Volume(Milliliters(100.0))
        }

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
