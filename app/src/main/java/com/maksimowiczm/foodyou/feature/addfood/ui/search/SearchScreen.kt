package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SearchScreen(
    onCloseClick: () -> Unit,
    onProductClick: (productId: Long) -> Unit,
    onCreateProduct: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val queryResult by viewModel.queryState.collectAsStateWithLifecycle()
    val measuredProducts by viewModel.measuredProducts.collectAsStateWithLifecycle()

    val searchState = rememberSearchState(
        meal = viewModel.meal,
        initialIsLoading = queryResult.isLoading,
        initialIsError = queryResult.error != null,
        initialData = queryResult.data,
        onQuickRemove = viewModel::onQuickRemove,
        onQuickAdd = viewModel::onQuickAdd,
        getRecentQueries = viewModel::getRecentQueries
    )

    LaunchedEffect(searchState.searchQuery) {
        viewModel.onSearch(searchState.searchQuery)
    }

    LaunchedEffect(queryResult) {
        searchState.onDataChange(queryResult)
    }

    BackHandler(
        enabled = searchState.searchQuery.isNotBlank()
    ) {
        searchState.onSearch("")
    }

    SearchScreen(
        measuredProducts = measuredProducts,
        searchState = searchState,
        onCloseClick = onCloseClick,
        onProductClick = onProductClick,
        onCreateProduct = onCreateProduct,
        onRetry = viewModel::onRetry,
        modifier = modifier
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SearchScreen(
    measuredProducts: List<ProductWithWeightMeasurement>,
    searchState: SearchState,
    onCloseClick: () -> Unit,
    onProductClick: (productId: Long) -> Unit,
    onCreateProduct: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        modifier = modifier,
        targetState = searchState.screen
    ) { screen ->
        when (screen) {
            SearchScreen.MAIN -> SearchScaffold(
                measuredProducts = measuredProducts,
                searchState = searchState,
                onRetry = onRetry,
                onProductClick = onProductClick,
                onCloseClick = onCloseClick,
                onCreateProduct = onCreateProduct,
                animatedVisibilityScope = this
            )

            SearchScreen.SEARCH -> SearchView(
                searchState = searchState,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(SearchSharedContentKeys.SEARCH_SEARCH_VIEW),
                    animatedVisibilityScope = this
                )
            )

            SearchScreen.BARCODE -> {
                BackHandler {
                    searchState.onBarcodeScannerClose()
                }

                CameraBarcodeScannerScreen(
                    onBarcodeScan = searchState::onSearch,
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(SearchSharedContentKeys.SEARCH_BARCODE_SCANNER),
                            animatedVisibilityScope = this
                        )
                )
            }
        }
    }
}
