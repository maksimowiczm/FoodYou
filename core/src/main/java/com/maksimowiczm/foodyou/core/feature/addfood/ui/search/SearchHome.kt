package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.QueryResult
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.MeasurementWithRank
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductIdWithMeasurementsIds
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.ui.modifier.horizontalDisplayCutoutPadding
import com.valentinilk.shimmer.Shimmer
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
    val queryResult by viewModel.pages.collectAsStateWithLifecycle()
    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    SearchHome(
        viewModel = viewModel,
        animatedVisibilityScope = animatedVisibilityScope,
        queryResult = queryResult,
        recentQueries = recentQueries,
        totalCalories = totalCalories,
        query = query,
        onProductClick = onProductClick,
        onQuickAdd = viewModel::onQuickAdd,
        onQuickRemove = viewModel::onQuickRemove,
        onSearchSettings = onSearchSettings,
        onSearch = viewModel::onSearch,
        onClearSearch = { viewModel.onSearch(null) },
        onRetry = viewModel::onRetry,
        onBack = onBack,
        onCreateProduct = onCreateProduct,
        onBarcodeScanner = onBarcodeScanner,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchHome(
    viewModel: SearchViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    queryResult: QueryResult<ProductIdWithMeasurementsIds>,
    recentQueries: List<ProductQuery>,
    totalCalories: Int,
    query: String?,
    onProductClick: (productId: Long) -> Unit,
    onQuickAdd: (productId: Long, measurement: WeightMeasurement) -> Unit,
    onQuickRemove: (measurementId: Long) -> Unit,
    onSearchSettings: () -> Unit,
    onSearch: (query: String) -> Unit,
    onClearSearch: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    val isEmpty by remember(queryResult) {
        derivedStateOf { queryResult.data.isEmpty() }
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

    val density = LocalDensity.current
    var errorCardHeight by remember { mutableIntStateOf(0) }
    val hasError = queryResult.error != null
    val anchoredDraggableState = rememberSaveable(
        hasError,
        saver = AnchoredDraggableState.Saver()
    ) {
        AnchoredDraggableState(
            initialValue = if (hasError) ErrorCardState.VISIBLE else ErrorCardState.HIDDEN_END
        )
    }
    val errorCard = @Composable {
        AnimatedVisibility(
            visible = hasError && anchoredDraggableState.settledValue == ErrorCardState.VISIBLE,
            modifier = Modifier
                .horizontalDisplayCutoutPadding()
                .fillMaxWidth()
                .onSizeChanged { errorCardHeight = it.height }
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

    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        .exclude(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))

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
                if (queryResult.isLoading) {
                    LoadingIndicator()
                }
            }

            if (isEmpty && !queryResult.isLoading) {
                Text(
                    text = stringResource(R.string.neutral_no_products_found),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            val shimmer = rememberShimmer(
                shimmerBounds = ShimmerBounds.Window
            )

            LazyColumn(
                modifier = Modifier.nestedScroll(bottomBarScrollBehavior.nestedScrollConnection),
                contentPadding = paddingValues
            ) {
                item {
                    Spacer(Modifier.height(density.run { errorCardHeight.toDp() }))
                }

                if (queryResult.data.isEmpty() && queryResult.isLoading) {
                    items(
                        count = 30
                    ) {
                        ProductSearchListItemSkeleton(
                            shimmer = shimmer,
                            containerColor = ProductSearchListItemDefaults.colors().uncheckedContainerColor
                        )
                    }
                }

                queryResult.data.forEach { model ->
                    if (model.measurements.isEmpty()) {
                        val rank = MeasurementWithRank.FIRST_RANK

                        item(
                            key = "${model.productId}-$rank"
                        ) {
                            ProductSearchListItem(
                                productMeasurementHolder = viewModel.holder(
                                    key = HolderKey(model.productId, rank),
                                    measurementId = null
                                ),
                                shimmer = shimmer,
                                onClick = { onProductClick(model.productId) },
                                onQuickAdd = { pId, wm ->
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                                    onQuickAdd(pId, wm)
                                },
                                onQuickRemove = {},
                                modifier = Modifier.animateItem()
                            )
                        }
                    } else {
                        model.measurements.forEach { measurement ->
                            item(
                                key = "${model.productId}-${measurement.rank}"
                            ) {
                                ProductSearchListItem(
                                    productMeasurementHolder = viewModel.holder(
                                        key = HolderKey(model.productId, measurement.rank),
                                        measurementId = measurement.measurementId
                                    ),
                                    shimmer = shimmer,
                                    onClick = { onProductClick(model.productId) },
                                    onQuickAdd = { _, _ -> },
                                    onQuickRemove = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                                        onQuickRemove(it)
                                    },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
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
private fun ProductSearchListItem(
    productMeasurementHolder: ProductMeasurementHolder,
    shimmer: Shimmer,
    onClick: () -> Unit,
    onQuickAdd: (productId: Long, measurement: WeightMeasurement) -> Unit,
    onQuickRemove: (measurementId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val model = productMeasurementHolder.model.collectAsStateWithLifecycle().value

    fun onCheckChange() {
        if (productMeasurementHolder.measurementId != null) {
            onQuickRemove(productMeasurementHolder.measurementId!!)
        } else if (model != null) {
            onQuickAdd(model.product.id, model.measurement)
        }
    }

    Crossfade(
        targetState = model != null,
        modifier = modifier
    ) {
        if (!it || model == null) {
            ProductSearchListItemSkeleton(
                shimmer = shimmer,
                containerColor = if (productMeasurementHolder.measurementId != null) {
                    ProductSearchListItemDefaults.colors().checkedContainerColor
                } else {
                    ProductSearchListItemDefaults.colors().uncheckedContainerColor
                }
            )
        } else {
            ProductSearchListItem(
                model = model,
                onClick = onClick,
                isChecked = productMeasurementHolder.measurementId != null,
                onCheckChange = { onCheckChange() }
            )
        }
    }
}

enum class ErrorCardState {
    HIDDEN_START,
    VISIBLE,
    HIDDEN_END
}
