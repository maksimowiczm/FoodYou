package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.diary.data.QueryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberSearchListState(
    lazyListState: LazyListState = rememberLazyListState(),
    initialProducts: List<ProductSearchUiModel> = emptyList(),
    initialIsLoading: Boolean = false,
    initialIsError: Boolean = false,
    onQuickAdd: suspend (ProductWithWeightMeasurement) -> Long = { 0 },
    onQuickRemove: (ProductWithWeightMeasurement) -> Unit = {},
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): SearchListState {
    return remember(
        lazyListState,
        initialProducts,
        onQuickAdd,
        onQuickRemove,
        coroutineScope
    ) {
        SearchListState(
            lazyListState = lazyListState,
            initialProducts = initialProducts,
            initialIsLoading = initialIsLoading,
            initialIsError = initialIsError,
            onQuickAdd = onQuickAdd,
            onQuickRemove = onQuickRemove,
            coroutineScope = coroutineScope
        )
    }
}

@Stable
class SearchListState(
    val lazyListState: LazyListState,
    initialIsLoading: Boolean,
    initialIsError: Boolean,
    initialProducts: List<ProductSearchUiModel>,
    private val onQuickAdd: suspend (ProductWithWeightMeasurement) -> Long,
    private val onQuickRemove: (ProductWithWeightMeasurement) -> Unit,
    private val coroutineScope: CoroutineScope
) {
    var isLoading: Boolean by mutableStateOf(initialIsLoading)
        private set

    var isError: Boolean by mutableStateOf(initialIsError)
        private set

    var products: List<ProductSearchUiModel> by mutableStateOf(initialProducts)
        private set

    fun onQueryResultChange(queryResult: QueryResult<List<ProductWithWeightMeasurement>>) {
        isLoading = queryResult.isLoading
        isError = queryResult.error != null

        val data = queryResult.data

        products = data.map {
            ProductSearchUiModel(
                model = it,
                isLoading = false,
                isChecked = it.measurementId != null
            )
        }
    }

    fun onProductCheckChange(index: Int, checked: Boolean) {
        val product = products[index]

        if (checked) {
            coroutineScope.launch {
                products = products.replaceIndexed(
                    product.copy(
                        isLoading = true
                    ),
                    index
                )

                val id = onQuickAdd(product.model)

                products = products.replaceIndexed(
                    product.copy(
                        model = product.model.copy(
                            measurementId = id
                        ),
                        isLoading = false,
                        isChecked = true
                    ),
                    index
                )
            }
        } else {
            products = products.replaceIndexed(
                product.copy(
                    model = product.model.copy(
                        measurementId = null
                    ),
                    isChecked = false
                ),
                index
            )

            onQuickRemove(product.model)
        }
    }
}

private fun <T> List<T>.replaceIndexed(newValue: T, index: Int): List<T> {
    return mapIndexed { i, value ->
        if (i == index) newValue else value
    }
}
