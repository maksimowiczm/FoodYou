package com.maksimowiczm.foodyou.feature.addfood.ui.search

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.ui.previewparameter.ProductWithWeightMeasurementPreviewParameter
import com.maksimowiczm.foodyou.ui.preview.SharedTransitionPreview
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SearchScaffold(
    measuredProducts: List<ProductWithWeightMeasurement>,
    searchState: SearchState,
    onRetry: () -> Unit,
    onProductClick: (productId: Long) -> Unit,
    onCloseClick: () -> Unit,
    onCreateProduct: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val topBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
            Surface(
                modifier = Modifier.clip(
                    shape = MaterialTheme.shapes.medium.copy(
                        topStart = CornerSize(0),
                        topEnd = CornerSize(0)
                    )
                ),
                color = BottomAppBarDefaults.containerColor
            ) {
                SearchTopBar(
                    modifier = Modifier.displayCutoutPadding(),
                    searchState = searchState,
                    onCloseClick = onCloseClick,
                    scrollBehavior = topBarScrollBehavior,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BottomAppBarDefaults.containerColor
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.clip(
                    shape = MaterialTheme.shapes.medium.copy(
                        bottomStart = CornerSize(0),
                        bottomEnd = CornerSize(0)
                    )
                ),
                color = BottomAppBarDefaults.containerColor
            ) {
                SearchBottomBar(
                    measuredProducts = measuredProducts,
                    searchState = searchState,
                    onCreateProduct = onCreateProduct,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                    )
                )
            }
        }
    ) { innerPadding ->
        ScaffoldContent(
            searchState = searchState,
            onProductClick = onProductClick,
            onRetry = onRetry,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .displayCutoutPadding()
                .fillMaxSize()
        )
    }
}

enum class ErrorCardState {
    HIDDEN_START,
    VISIBLE,
    HIDDEN_END
}

@Composable
private fun ScaffoldContent(
    searchState: SearchState,
    onRetry: () -> Unit,
    onProductClick: (productId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val anchoredDraggableState = remember(searchState.isError) {
        AnchoredDraggableState(
            initialValue = if (searchState.isError) ErrorCardState.VISIBLE else ErrorCardState.HIDDEN_END
        )
    }

    val lazyListState = rememberLazyListState()
    val flingBehavior = ScrollableDefaults.flingBehavior()

    Column(
        modifier = modifier
            .onSizeChanged {
                val draggableAnchors = DraggableAnchors {
                    ErrorCardState.HIDDEN_START at -it.width.toFloat()
                    ErrorCardState.VISIBLE at 0f
                    ErrorCardState.HIDDEN_END at it.width.toFloat()
                }

                anchoredDraggableState.updateAnchors(draggableAnchors)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = searchState.isError &&
                anchoredDraggableState.settledValue == ErrorCardState.VISIBLE,
            modifier = Modifier
                .padding(8.dp)
                .draggable(
                    state = rememberDraggableState { delta ->
                        lazyListState.dispatchRawDelta(-delta)
                    },
                    orientation = Orientation.Vertical,
                    onDragStopped = { velocity ->
                        lazyListState.scroll {
                            with(flingBehavior) {
                                performFling(-velocity)
                            }
                        }
                    }
                )
        ) {
            FoodDatabaseErrorCard(
                modifier = Modifier
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        orientation = Orientation.Horizontal
                    )
                    .offset {
                        val x = anchoredDraggableState.offset.roundToInt()
                        IntOffset(x, 0)
                    },
                onRetry = onRetry
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (searchState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(8.dp)
                        .size(24.dp)
                        .zIndex(1f)
                )
            }

            if (searchState.data.isEmpty() && !searchState.isLoading) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.feedback_no_products_found)
                )
            }

            ListCard(
                searchState = searchState,
                onProductClick = onProductClick,
                lazyListState = lazyListState,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun ListCard(
    searchState: SearchState,
    onProductClick: (productId: Long) -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        LazyColumn(
            modifier = Modifier,
            state = lazyListState
        ) {
            itemsIndexed(
                items = searchState.data
            ) { index, uiModel ->
                ProductSearchListItem(
                    uiModel = uiModel,
                    onClick = { onProductClick(uiModel.model.product.id) },
                    onCheckChange = { searchState.onCheckChange(index, it) },
                    colors = ProductSearchListItemDefaults.colors(
                        uncheckedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        uncheckedContentColor = MaterialTheme.colorScheme.onSurface,
                        checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        checkedToggleButtonContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        checkedToggleButtonContentColor = MaterialTheme.colorScheme.onTertiaryContainer
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
@Preview(
    showSystemUi = true
)
@Composable
private fun SearchScaffoldPreview() {
    val data = ProductWithWeightMeasurementPreviewParameter().values.toList()

    SharedTransitionPreview {
        SearchScaffold(
            measuredProducts = data.filter { it.measurementId != null },
            searchState = rememberSearchState(
                meal = Meal.Breakfast,
                initialIsLoading = true,
                initialIsError = true,
                initialData = data,
                onQuickRemove = {},
                onQuickAdd = { 0 }
            ),
            onRetry = {},
            onProductClick = {},
            onCloseClick = {},
            onCreateProduct = {},
            animatedVisibilityScope = this@SharedTransitionPreview
        )
    }
}
