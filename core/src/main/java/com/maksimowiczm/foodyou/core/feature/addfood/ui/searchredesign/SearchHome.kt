package com.maksimowiczm.foodyou.core.feature.addfood.ui.searchredesign

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
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
                            if (isChecked) {
                                viewModel.onQuickRemove(
                                    model = item
                                )
                            } else {
                                viewModel.onQuickAdd(
                                    productId = item.product.id,
                                    measurement = item.measurement
                                )
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
