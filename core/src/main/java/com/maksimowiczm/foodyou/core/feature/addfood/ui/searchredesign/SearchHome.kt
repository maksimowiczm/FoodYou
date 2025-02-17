package com.maksimowiczm.foodyou.core.feature.addfood.ui.searchredesign

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.ProductSearchListItem
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.ProductSearchUiModel
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchBottomBar
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchTopBar
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.rememberSearchTopBarState

@Composable
fun SearchHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: SearchViewModel,
    onProductClick: (productId: Long) -> Unit,
    onProductLongClick: (productId: Long) -> Unit,
    onSearchSettings: () -> Unit,
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val productsWithMeasurements = viewModel.productsWithMeasurements.collectAsLazyPagingItems()
    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    SearchHome(
        animatedVisibilityScope = animatedVisibilityScope,
        productsWithMeasurements = productsWithMeasurements,
        totalCalories = totalCalories,
        recentQueries = recentQueries,
        query = query,
        onProductClick = onProductClick,
        onProductLongClick = onProductLongClick,
        onQuickAdd = viewModel::onQuickAdd,
        onQuickRemove = viewModel::onQuickRemove,
        onSearchSettings = onSearchSettings,
        onSearch = viewModel::onSearch,
        onClearSearch = { viewModel.onSearch(null) },
        onBack = onBack,
        onCreateProduct = onCreateProduct,
        onBarcodeScanner = onBarcodeScanner,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    productsWithMeasurements: LazyPagingItems<ProductWithWeightMeasurement>,
    recentQueries: List<ProductQuery>,
    totalCalories: Int,
    query: String?,
    onProductClick: (productId: Long) -> Unit,
    onProductLongClick: (productId: Long) -> Unit,
    onQuickAdd: (productId: Long, measurement: WeightMeasurement) -> Unit,
    onQuickRemove: (measurementId: Long) -> Unit,
    onSearchSettings: () -> Unit,
    onSearch: (query: String) -> Unit,
    onClearSearch: () -> Unit,
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmpty by remember(productsWithMeasurements.loadState) {
        derivedStateOf {
            productsWithMeasurements.loadState.refresh != LoadState.Loading && productsWithMeasurements.itemCount == 0
        }
    }

    val topBar = @Composable {
        SearchTopBar(
            state = rememberSearchTopBarState(
                query = query,
                recentQueries = recentQueries
            ),
            onSearchSettings = onSearchSettings,
            onSearch = onSearch,
            onClearSearch = onClearSearch,
            onBack = onBack
        )
    }

    val bottomBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    val bottomBar = @Composable {
        SearchBottomBar(
            animatedVisibilityScope = animatedVisibilityScope,
            totalCalories = totalCalories,
            onCreateProduct = onCreateProduct,
            onBarcodeScanner = onBarcodeScanner,
            scrollBehavior = bottomBarScrollBehavior
        )
    }
    // Make sure that bottom bar is visible when user can't scroll
    LaunchedEffect(productsWithMeasurements.itemCount) {
        if (productsWithMeasurements.itemCount == 0) {
            bottomBarScrollBehavior.nestedScrollConnection.onPostScroll(
                consumed = Offset.Infinite,
                available = Offset.Infinite,
                source = NestedScrollSource.UserInput
            )
            bottomBarScrollBehavior.nestedScrollConnection.onPostFling(
                consumed = Velocity.Zero,
                available = Velocity.Zero
            )
        }
    }

    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        .exclude(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (productsWithMeasurements.loadState.refresh == LoadState.Loading) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .fillMaxWidth()
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }

            if (isEmpty) {
                Text(
                    text = stringResource(R.string.neutral_no_products_found),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.nestedScroll(bottomBarScrollBehavior.nestedScrollConnection)
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

                item {
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
                }
            }
        }
    }
}
