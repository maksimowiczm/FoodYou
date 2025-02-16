package com.maksimowiczm.foodyou.core.feature.addfood.ui.searchredesign

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchHome(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel<SearchViewModel>()
) {
    val measurementIds by viewModel.measurements.collectAsStateWithLifecycle()
    val productIds = viewModel.productIds

    val state = rememberSearchState(
        measurements = measurementIds,
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
                            measurementId = measurement.id
                        )
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
                    )
                )
            }
        }
    }
}
