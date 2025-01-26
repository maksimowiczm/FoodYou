package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.addfood.data.QueryResult
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberSearchState(
    initialQuery: String = "",
    initialRecentQueries: List<ProductQuery> = emptyList(),
    initialQueryResult: QueryResult<List<ProductWithWeightMeasurement>> =
        QueryResult.loading(emptyList()),
    onQuickAdd: suspend (ProductWithWeightMeasurement) -> Long = { 0 },
    onQuickRemove: (ProductWithWeightMeasurement) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    lazyListState: LazyListState = rememberLazyListState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
): SearchState {
    return rememberSaveable(
        onQuickAdd,
        onQuickRemove,
        lazyListState,
        coroutineScope,
        saver = Saver(
            save = {
                arrayOf<Any>(
                    it.query
                )
            },
            restore = {
                SearchState(
                    initialQuery = it[0] as String,
                    initialRecentQueries = initialRecentQueries,
                    initialQueryResult = initialQueryResult,
                    onQuickRemove = onQuickRemove,
                    onQuickAdd = onQuickAdd,
                    onSearch = onSearch,
                    onRetry = onRetry,
                    lazyListState = lazyListState,
                    coroutineScope = coroutineScope,
                    navController = navController
                )
            }
        )
    ) {
        SearchState(
            initialQuery = initialQuery,
            initialRecentQueries = initialRecentQueries,
            initialQueryResult = initialQueryResult,
            onQuickAdd = onQuickAdd,
            onQuickRemove = onQuickRemove,
            onSearch = onSearch,
            onRetry = onRetry,
            lazyListState = lazyListState,
            coroutineScope = coroutineScope,
            navController = navController
        )
    }
}

enum class SearchScreen(
    val route: String
) {
    Home("home"),
    BarcodeScanner("barcode-scanner")
}

class SearchState(
    initialQuery: String,
    initialRecentQueries: List<ProductQuery>,
    initialQueryResult: QueryResult<List<ProductWithWeightMeasurement>>,
    val lazyListState: LazyListState,
    private val coroutineScope: CoroutineScope,
    private val onQuickAdd: suspend (ProductWithWeightMeasurement) -> Long,
    private val onQuickRemove: (ProductWithWeightMeasurement) -> Unit,
    onSearch: (String) -> Unit,
    onRetry: () -> Unit,
    val navController: NavHostController
) {
    fun navigateToHome() {
        navController.navigate(
            route = SearchScreen.Home.route,
            navOptions = navOptions {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        )
    }

    fun navigateToBarcodeScanner() {
        navController.navigate(
            route = SearchScreen.BarcodeScanner.route
        )
    }

    var query by mutableStateOf(initialQuery)
        private set

    private val _onSearch = onSearch

    fun onSearch(query: String) {
        this.query = query.trim()
        _onSearch(query)
    }

    private val _onRetry = onRetry

    fun onRetry() {
        _onRetry()
    }

    var isLoading: Boolean by mutableStateOf(initialQueryResult.isLoading)
        private set

    var isError: Boolean by mutableStateOf(initialQueryResult.error != null)
        private set

    var products by mutableStateOf(
        initialQueryResult.data.map {
            ProductSearchUiModel(
                model = it,
                isLoading = false,
                isChecked = it.measurementId != null
            )
        }
    )

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
            onQuickRemove(product.model)

            products = products.replaceIndexed(
                product.copy(
                    model = product.model.copy(
                        measurementId = null
                    ),
                    isChecked = false
                ),
                index
            )
        }
    }

    var recentQueries by mutableStateOf(initialRecentQueries)
        private set

    fun updateRecentQueries(queries: List<ProductQuery>) {
        recentQueries = queries
    }
}

private fun <T> List<T>.replaceIndexed(newValue: T, index: Int): List<T> {
    return mapIndexed { i, value ->
        if (i == index) newValue else value
    }
}
