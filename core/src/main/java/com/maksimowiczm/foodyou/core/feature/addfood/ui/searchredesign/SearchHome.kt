package com.maksimowiczm.foodyou.core.feature.addfood.ui.searchredesign

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey

@Composable
fun SearchHome(
    viewModel: SearchViewModel,
    onProductClick: (productId: Long) -> Unit,
    onProductLongClick: (productId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val measurementIds by viewModel.measurements.collectAsStateWithLifecycle(null)
    val productIds = viewModel.productIds

    if (measurementIds == null) {
        return
    }

    val state = rememberSearchState(
        measurements = measurementIds!!,
        productIds = productIds
    )

    val pagingItems = state.productIds.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues
        ) {
            state.measurements.forEach { measurement ->
                item {
                    SearchListItem(
                        viewModel = viewModel.itemViewModel(
                            productId = measurement.productId,
                            measurementId = measurement.weightMeasurementId
                        ),
                        onClick = { onProductClick(measurement.productId) },
                        onLongClick = { onProductLongClick(measurement.productId) }
                    )
                }
            }

            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey()
            ) { index ->
                val item = pagingItems[index] ?: return@items

                if (state.measurements.any { it.productId == item }) {
                    return@items
                }

                SearchListItem(
                    viewModel = viewModel.itemViewModel(
                        productId = item
                    ),
                    onClick = { onProductClick(item) },
                    onLongClick = { onProductLongClick(item) }
                )
            }
        }
    }
}
