package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.feature.addfood.data.QueryResult
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

enum class SearchScreen {
    MAIN,
    SEARCH,
    BARCODE
}

@Composable
fun rememberSearchState(
    meal: Meal,
    initialIsLoading: Boolean,
    initialIsError: Boolean,
    initialData: List<ProductWithWeightMeasurement> = emptyList(),
    onQuickRemove: (ProductWithWeightMeasurement) -> Unit,
    onQuickAdd: suspend (ProductWithWeightMeasurement) -> Long,
    getRecentQueries: () -> Flow<List<String>> = { flow { } }
): SearchState {
    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        meal,
        coroutineScope,
        onQuickRemove,
        onQuickAdd,
        getRecentQueries,
        saver = Saver(
            save = {
                arrayOf<Any?>(
                    it.screen,
                    it.searchQuery
                )
            },
            restore = {
                SearchState(
                    meal = meal,
                    initialShowSearchScreen = it[0] as SearchScreen,
                    initialSearchQuery = it[1] as String,
                    initialIsLoading = initialIsLoading,
                    initialIsError = initialIsError,
                    initialData = initialData,
                    coroutineScope = coroutineScope,
                    onQuickRemove = onQuickRemove,
                    onQuickAdd = onQuickAdd,
                    getRecentQueries = getRecentQueries
                )
            }
        )
    ) {
        SearchState(
            meal = meal,
            initialShowSearchScreen = SearchScreen.MAIN,
            initialSearchQuery = "",
            initialIsLoading = initialIsLoading,
            initialIsError = initialIsError,
            initialData = initialData,
            coroutineScope = coroutineScope,
            onQuickRemove = onQuickRemove,
            onQuickAdd = onQuickAdd,
            getRecentQueries = getRecentQueries
        )
    }
}

class SearchState(
    val meal: Meal,
    initialShowSearchScreen: SearchScreen,
    initialSearchQuery: String,
    initialIsLoading: Boolean,
    initialIsError: Boolean,
    initialData: List<ProductWithWeightMeasurement>,
    private val coroutineScope: CoroutineScope,
    private val onQuickRemove: (ProductWithWeightMeasurement) -> Unit,
    private val onQuickAdd: suspend (ProductWithWeightMeasurement) -> Long,
    getRecentQueries: () -> Flow<List<String>>
) {
    private val _getRecentQueries = getRecentQueries
    fun getRecentQueries(): Flow<List<String>> = _getRecentQueries()

    var screen: SearchScreen by mutableStateOf(initialShowSearchScreen)
        private set
    var searchQuery: String by mutableStateOf(initialSearchQuery)
        private set

    fun onSearchClick() {
        screen = SearchScreen.SEARCH
    }

    fun onSearchClose() {
        screen = SearchScreen.MAIN
    }

    fun onSearch(query: String) {
        searchQuery = query.trim()
        screen = SearchScreen.MAIN
    }

    fun onBarcodeScannerClick() {
        screen = SearchScreen.BARCODE
    }

    fun onBarcodeScannerClose() {
        screen = SearchScreen.MAIN
    }

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

    var isLoading: Boolean by mutableStateOf(initialIsLoading)
        private set

    var isError: Boolean by mutableStateOf(initialIsError)
        private set

    fun onDataChange(queryResult: QueryResult<List<ProductWithWeightMeasurement>>) {
        isLoading = queryResult.isLoading
        isError = queryResult.error != null

        val data = queryResult.data

        this.data = data.map {
            ProductSearchUiModel(
                model = it,
                isLoading = false,
                isChecked = it.measurementId != null
            )
        }
    }

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
