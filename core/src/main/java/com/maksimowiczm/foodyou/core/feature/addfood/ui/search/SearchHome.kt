package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.core.ui.performToggle
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
fun SearchHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: SearchViewModel,
    onProductClick: (productId: Long) -> Unit,
    onSearchSettings: () -> Unit,
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val productsWithMeasurements = viewModel.productsWithMeasurements.collectAsLazyPagingItems(
        viewModel.viewModelScope.coroutineContext
    )
    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    SearchHome(
        animatedVisibilityScope = animatedVisibilityScope,
        pages = productsWithMeasurements,
        totalCalories = totalCalories,
        recentQueries = recentQueries,
        query = query,
        onProductClick = onProductClick,
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
    pages: LazyPagingItems<ProductWithWeightMeasurement>,
    recentQueries: List<ProductQuery>,
    totalCalories: Int,
    query: String?,
    onProductClick: (productId: Long) -> Unit,
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
    val hapticFeedback = LocalHapticFeedback.current

    val isEmpty by remember(pages.loadState) {
        derivedStateOf { pages.itemCount == 0 }
    }
    val isLoading by remember(pages.loadState) {
        derivedStateOf {
            pages.loadState.refresh == LoadState.Loading || pages.loadState.append == LoadState.Loading
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
    LaunchedEffect(isEmpty) {
        if (isEmpty) {
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

    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        .exclude(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))

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
        bottomBar = bottomBar,
        contentWindowInsets = contentWindowInsets
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

                item {
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
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
