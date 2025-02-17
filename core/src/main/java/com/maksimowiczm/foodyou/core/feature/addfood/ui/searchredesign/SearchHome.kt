package com.maksimowiczm.foodyou.core.feature.addfood.ui.searchredesign

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.ProductSearchListItem
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.ProductSearchUiModel

@Composable
fun SearchHome(
    viewModel: SearchViewModel,
    onProductClick: (productId: Long) -> Unit,
    onProductLongClick: (productId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val productsWithMeasurements = viewModel.productsWithMeasurements.collectAsLazyPagingItems()

    SearchHome(
        productsWithMeasurements = productsWithMeasurements,
        onProductClick = onProductClick,
        onProductLongClick = onProductLongClick,
        onQuickAdd = viewModel::onQuickAdd,
        onQuickRemove = viewModel::onQuickRemove,
        modifier = modifier
    )
}

@Composable
private fun SearchHome(
    productsWithMeasurements: LazyPagingItems<ProductWithWeightMeasurement>,
    onProductClick: (productId: Long) -> Unit,
    onProductLongClick: (productId: Long) -> Unit,
    onQuickAdd: (productId: Long, measurement: WeightMeasurement) -> Unit,
    onQuickRemove: (measurementId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
        ) {
            items(
                count = productsWithMeasurements.itemCount,
                key = productsWithMeasurements.itemKey { it.product.id }
            ) {
                val item = productsWithMeasurements[it]

                if (item == null) {
                    // TODO
                    Text("I am a placeholder")
                } else {
                    val isChecked = item.measurementId != null

                    ProductSearchListItem(
                        uiModel = ProductSearchUiModel(
                            model = item,
                            isLoading = false,
                            isChecked = isChecked
                        ),
                        onCheckChange = {
                            if (item.measurementId != null) {
                                onQuickRemove(item.measurementId)
                            } else {
                                onQuickAdd(item.product.id, item.measurement)
                            }
                        },
                        onClick = { onProductClick(item.product.id) },
                        onLongClick = { onProductLongClick(item.product.id) },
                        modifier = Modifier
                            .animateItem(
                                placementSpec = tween(
                                    easing = FastOutLinearInEasing
                                )
                            )
                            .zIndex(if (isChecked) 1f else 0f)
                    )
                }
            }
        }
    }
}
