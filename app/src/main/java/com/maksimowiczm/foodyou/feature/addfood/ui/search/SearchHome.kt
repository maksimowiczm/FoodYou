package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.ui.performToggle
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchHome(
    onProductClick: (epochDay: Int, mealId: Long, productId: Long) -> Unit,
    onBack: () -> Unit,
    onCreateProduct: (epochDay: Int, mealId: Long) -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val productsWithMeasurements = viewModel.productsWithMeasurements.collectAsLazyPagingItems(
        viewModel.viewModelScope.coroutineContext
    )
    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    SearchHome(
        pages = productsWithMeasurements,
        recentQueries = recentQueries,
        query = query,
        onProductClick = {
            onProductClick(viewModel.date.toEpochDays(), viewModel.mealId, it)
        },
        onQuickAdd = viewModel::onQuickAdd,
        onQuickRemove = viewModel::onQuickRemove,
        onSearch = viewModel::onSearch,
        onClearSearch = { viewModel.onSearch(null) },
        onBack = onBack,
        onCreateProduct = {
            onCreateProduct(viewModel.date.toEpochDays(), viewModel.mealId)
        },
        onBarcodeScanner = onBarcodeScanner,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchHome(
    pages: LazyPagingItems<ProductWithWeightMeasurement>,
    recentQueries: List<ProductQuery>,
    query: String?,
    onProductClick: (productId: Long) -> Unit,
    onQuickAdd: (productId: Long, measurement: WeightMeasurement) -> Unit,
    onQuickRemove: (measurementId: Long) -> Unit,
    onSearch: (query: String) -> Unit,
    onClearSearch: () -> Unit,
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    val isEmpty by remember(pages.loadState) {
        derivedStateOf { pages.itemCount == 0 }
    }
    val isLoading by remember(pages.loadState) {
        derivedStateOf {
            pages.loadState.refresh == LoadState.Loading ||
                pages.loadState.append == LoadState.Loading
        }
    }
    val hasError by remember(pages.loadState) {
        derivedStateOf { pages.loadState.hasError }
    }

    val topBar = @Composable {
        SearchTopBar(
            state = rememberSearchTopBarState(
                query = query,
                recentQueries = recentQueries
            ),
            onBarcodeScanner = onBarcodeScanner,
            onSearch = onSearch,
            onClearSearch = onClearSearch,
            onBack = onBack
        )
    }

    var errorCardHeight by remember { mutableIntStateOf(0) }
    val anchoredDraggableState = rememberSaveable(
        saver = AnchoredDraggableState.Saver()
    ) {
        AnchoredDraggableState(
            initialValue = if (hasError) ErrorCardState.VISIBLE else ErrorCardState.HIDDEN_END
        )
    }
    val errorCard = @Composable {
        ErrorCard(
            anchoredDraggableState = anchoredDraggableState,
            hasError = hasError,
            onRetry = pages::retry,
            modifier = Modifier.onSizeChanged { errorCardHeight = it.height }
        )
    }

    val fab = @Composable {
        FloatingActionButton(
            onClick = onCreateProduct
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.action_create_new_product)
            )
        }
    }

    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )

    Scaffold(
        modifier = modifier.onSizeChanged {
            val draggableAnchors = DraggableAnchors {
                ErrorCardState.HIDDEN_START at -it.width.toFloat()
                ErrorCardState.VISIBLE at 0f
                ErrorCardState.HIDDEN_END at it.width.toFloat()
            }

            anchoredDraggableState.updateAnchors(draggableAnchors)
        },
        topBar = topBar,
        floatingActionButton = fab
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .fillMaxWidth()
                    .zIndex(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                errorCard()
                AnimatedVisibility(
                    visible = isLoading
                ) {
                    LoadingIndicator()
                }
            }

            if (isEmpty && pages.loadState.append != LoadState.Loading) {
                Text(
                    text = stringResource(R.string.neutral_no_products_found),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                contentPadding = paddingValues
            ) {
                item {
                    Spacer(Modifier.height(LocalDensity.current.run { errorCardHeight.toDp() }))
                }

                if (pages.loadState.refresh == LoadState.Loading && isEmpty) {
                    items(
                        count = 100,
                        key = { "skeleton-refresh-$it" }
                    ) {
                        ProductSearchListItemSkeleton(shimmer = shimmer)
                    }
                }

                items(
                    count = pages.itemCount,
                    key = pages.itemKey {
                        "${it.product.id} ${it.measurementId}"
                    }
                ) {
                    Crossfade(
                        targetState = pages[it],
                        // Do only placement animation
                        modifier = Modifier.animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = null
                        )
                    ) { target ->
                        if (target == null) {
                            ProductSearchListItemSkeleton(
                                shimmer = shimmer
                            )
                        } else {
                            ProductSearchListItem(
                                model = target,
                                isChecked = target.measurementId != null,
                                onCheckChange = { newState ->
                                    hapticFeedback.performToggle(newState)

                                    if (target.measurementId != null) {
                                        onQuickRemove(target.measurementId)
                                    } else {
                                        onQuickAdd(target.product.id, target.measurement)
                                    }
                                },
                                onClick = { onProductClick(target.product.id) }
                            )
                        }
                    }
                }

                if (pages.loadState.append == LoadState.Loading) {
                    items(
                        count = 3,
                        key = { "skeleton-append-$it" }
                    ) {
                        ProductSearchListItemSkeleton(shimmer = shimmer)
                    }
                }

                // FAB spacer
                item {
                    Spacer(Modifier.height(8.dp))
                    Spacer(Modifier.height(56.dp))
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(
    anchoredDraggableState: AnchoredDraggableState<ErrorCardState>,
    hasError: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(hasError) {
        anchoredDraggableState.snapTo(
            if (hasError) ErrorCardState.VISIBLE else ErrorCardState.HIDDEN_END
        )
    }

    AnimatedVisibility(
        visible = hasError && anchoredDraggableState.settledValue == ErrorCardState.VISIBLE,
        modifier = modifier
            .horizontalDisplayCutoutPadding()
            .fillMaxWidth()
            .anchoredDraggable(
                state = anchoredDraggableState,
                orientation = Orientation.Horizontal
            )
    ) {
        FoodDatabaseErrorCard(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp)
                .offset {
                    IntOffset(
                        x = anchoredDraggableState.requireOffset().fastRoundToInt(),
                        y = 0
                    )
                },
            onRetry = onRetry
        )
    }
}

private enum class ErrorCardState {
    HIDDEN_START,
    VISIBLE,
    HIDDEN_END
}
