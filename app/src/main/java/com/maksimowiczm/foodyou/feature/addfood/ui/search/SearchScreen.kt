package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductSearchModel
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.toSearchModel
import com.maksimowiczm.foodyou.feature.product.ui.previewparameter.ProductPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun SearchScreen(
    onCloseClick: () -> Unit,
    onProductClick: (productId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val meal = viewModel.meal
    val queryResult by viewModel.queryState.collectAsStateWithLifecycle()
    val searchState = rememberSearchItemsState(
        queryResult = queryResult,
        onQuickAdd = viewModel::onQuickAdd,
        onQuickRemove = viewModel::onQuickRemove
    )

    SearchScreen(
        searchItemsState = searchState,
        meal = meal,
        onCloseClick = onCloseClick,
        onSearch = viewModel::onSearch,
        onRetry = viewModel::onRetry,
        onProductClick = onProductClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    searchItemsState: SearchItemsState,
    meal: Meal,
    onProductClick: (productId: Long) -> Unit,
    onCloseClick: () -> Unit,
    onSearch: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .displayCutoutPadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SearchTopBar(
                meal = meal,
                onCloseClick = onCloseClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        ScaffoldContent(
            searchItemsState = searchItemsState,
            onSearch = onSearch,
            onProductClick = onProductClick,
            onRetry = onRetry,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
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
    searchItemsState: SearchItemsState,
    onSearch: (String) -> Unit,
    onRetry: () -> Unit,
    onProductClick: (productId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val anchoredDraggableState = remember(searchItemsState.isError) {
        AnchoredDraggableState(
            initialValue = if (searchItemsState.isError) ErrorCardState.VISIBLE else ErrorCardState.HIDDEN_END
        )
    }

    Column(
        modifier = modifier.onSizeChanged {
            val draggableAnchors = DraggableAnchors {
                ErrorCardState.HIDDEN_START at -it.width.toFloat()
                ErrorCardState.VISIBLE at 0f
                ErrorCardState.HIDDEN_END at it.width.toFloat()
            }

            anchoredDraggableState.updateAnchors(draggableAnchors)
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            onSearch = onSearch
        )

        AnimatedVisibility(
            visible = searchItemsState.isError &&
                anchoredDraggableState.settledValue == ErrorCardState.VISIBLE,
            modifier = Modifier.padding(8.dp)
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
            if (searchItemsState.isLoading) {
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

            if (searchItemsState.data.isEmpty() && !searchItemsState.isLoading) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.feedback_no_products_found)
                )
            } else {
                ListCard(
                    searchItemsState = searchItemsState,
                    onProductClick = onProductClick,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun ListCard(
    searchItemsState: SearchItemsState,
    onProductClick: (productId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        LazyColumn(
            modifier = Modifier
        ) {
            itemsIndexed(
                items = searchItemsState.data
            ) { index, uiModel ->
                ProductSearchListItem(
                    uiModel = uiModel,
                    onClick = { onProductClick(uiModel.model.product.id) },
                    onCheckChange = { searchItemsState.onCheckChange(index, it) },
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

@PreviewLightDark
@Composable
fun SearchScreenPreview() {
    val data = ProductPreviewParameterProvider().values.mapIndexed { index, it ->
        ProductSearchModel(
            product = it.toSearchModel(),
            measurementId = if (index % 3 == 0) 1L else null,
            measurement = WeightMeasurement.WeightUnit(100f)
        )
    }.toList()

    FoodYouTheme {
        SearchScreen(
            searchItemsState = SearchItemsState(
                isLoading = true,
                isError = true,
                initialData = data,
                onQuickRemove = {},
                onQuickAdd = { 0L },
                coroutineScope = rememberCoroutineScope()
            ),
            meal = Meal.Breakfast,
            onProductClick = {},
            onCloseClick = {},
            onSearch = {},
            onRetry = {}
        )
    }
}
