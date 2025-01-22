package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.feature.addfood.data.QueryResult
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductSearchModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberSearchItemsState(
    queryResult: QueryResult<List<ProductSearchModel>>,
    onQuickAdd: suspend (ProductSearchModel) -> Long,
    onQuickRemove: (ProductSearchModel) -> Unit
): SearchItemsState {
    val coroutineScope = rememberCoroutineScope()

    return remember(
        queryResult,
        onQuickRemove,
        onQuickAdd
    ) {
        SearchItemsState(
            isLoading = queryResult.isLoading,
            isError = queryResult.error != null,
            initialData = queryResult.data,
            onQuickRemove = onQuickRemove,
            onQuickAdd = onQuickAdd,
            coroutineScope = coroutineScope
        )
    }
}

class SearchItemsState(
    val isLoading: Boolean,
    val isError: Boolean,
    initialData: List<ProductSearchModel>,
    private val coroutineScope: CoroutineScope,
    private val onQuickRemove: (ProductSearchModel) -> Unit,
    private val onQuickAdd: suspend (ProductSearchModel) -> Long
) {
    var data: List<ProductSearchUiModel> by mutableStateOf(
        initialData.map {
            ProductSearchUiModel(
                model = it,
                isLoading = false,
                isChecked = it.measurementId != null
            )
        }
    )
        private set

    fun onCheckChange(index: Int, checked: Boolean) {
        val modelState = data[index]

        if (checked) {
            coroutineScope.launch {
                data = data.replaceIndexed(
                    modelState.copy(
                        isLoading = true
                    ),
                    index
                )

                val id = onQuickAdd(modelState.model)

                data = data.replaceIndexed(
                    modelState.copy(
                        model = modelState.model.copy(
                            measurementId = id
                        ),
                        isLoading = false,
                        isChecked = true
                    ),
                    index
                )
            }
        } else {
            onQuickRemove(modelState.model)

            data = data.replaceIndexed(
                modelState.copy(
                    model = modelState.model.copy(
                        measurementId = null
                    ),
                    isChecked = false
                ),
                index
            )
        }
    }
}

private fun <T> List<T>.replaceIndexed(newValue: T, index: Int): List<T> {
    return mapIndexed { i, value ->
        if (i == index) newValue else value
    }
}
