package com.maksimowiczm.foodyou.feature.addfood.ui.search

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maksimowiczm.foodyou.feature.addfood.data.QueryResult
import com.maksimowiczm.foodyou.feature.addfood.ui.AddFoodSharedTransitionKeys
import com.maksimowiczm.foodyou.feature.addfood.ui.previewparameter.ProductWithWeightMeasurementPreviewParameter
import com.maksimowiczm.foodyou.ui.LocalSharedTransitionScope
import com.maksimowiczm.foodyou.ui.preview.SharedTransitionPreview
import com.maksimowiczm.foodyou.ui.preview.asList
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    onCreateProduct: () -> Unit,
    onProductClick: (productId: Long) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    val queries by viewModel.recentQueries.collectAsStateWithLifecycle()
    val queryResult by viewModel.queryState.collectAsStateWithLifecycle()
    val searchState = rememberSearchState(
        initialRecentQueries = queries,
        initialQueryResult = queryResult,
        onQuickAdd = viewModel::onQuickAdd,
        onQuickRemove = viewModel::onQuickRemove,
        onSearch = viewModel::onSearch,
        onRetry = viewModel::onRetry
    )

    LaunchedEffect(searchState, queries) {
        searchState.updateRecentQueries(queries)
    }

    LaunchedEffect(queryResult) {
        searchState.onQueryResultChange(queryResult)
    }

    SearchScreen(
        totalCalories = totalCalories,
        onCreateProduct = onCreateProduct,
        onProductClick = onProductClick,
        onClose = onClose,
        modifier = modifier,
        searchState = searchState
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SearchScreen(
    totalCalories: Int,
    onCreateProduct: () -> Unit,
    onProductClick: (productId: Long) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    searchState: SearchState = rememberSearchState()
) {
    // Use NavHost because I don't have to implement predictive back this way

    NavHost(
        modifier = modifier,
        navController = searchState.navController,
        startDestination = SearchScreen.Home.route
    ) {
        composable(SearchScreen.Home.route) {
            SearchHome(
                animatedVisibilityScope = this,
                totalCalories = totalCalories,
                onCreateProduct = onCreateProduct,
                onProductClick = { model ->
                    onProductClick(model.model.product.id)
                },
                onBack = onClose,
                searchState = searchState
            )
        }
        composable(SearchScreen.BarcodeScanner.route) {
            val sharedTransitionScope =
                LocalSharedTransitionScope.current ?: error("No shared transition scope found")

            with(sharedTransitionScope) {
                CameraBarcodeScannerScreen(
                    onBarcodeScan = {
                        searchState.navigateToHome()
                        searchState.onSearch(it)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                AddFoodSharedTransitionKeys.BARCODE_SCANNER
                            ),
                            animatedVisibilityScope = this@composable
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SearchScreenPreview() {
    val products = ProductWithWeightMeasurementPreviewParameter().asList()

    FoodYouTheme {
        SharedTransitionPreview { _, _ ->
            SearchScreen(
                totalCalories = 678,
                onCreateProduct = {},
                onProductClick = {},
                onClose = {},
                searchState = rememberSearchState(
                    initialQueryResult = QueryResult.success(products)
                )
            )
        }
    }
}
