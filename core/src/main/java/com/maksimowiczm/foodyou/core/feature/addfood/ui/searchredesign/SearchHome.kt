package com.maksimowiczm.foodyou.core.feature.addfood.ui.searchredesign

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
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

    SearchHome(
        animatedVisibilityScope = animatedVisibilityScope,
        productsWithMeasurements = productsWithMeasurements,
        totalCalories = totalCalories,
        recentQueries = recentQueries,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    productsWithMeasurements: LazyPagingItems<ProductWithWeightMeasurement>,
    recentQueries: List<ProductQuery>,
    totalCalories: Int,
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
    val topBar = @Composable {
        SearchTopBar(
            state = rememberSearchTopBarState(
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

    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        .exclude(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
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
