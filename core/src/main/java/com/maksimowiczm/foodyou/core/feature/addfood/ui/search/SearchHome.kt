package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.core.feature.addfood.ui.AddFoodState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.previewparameter.ProductSearchUiModelPreviewParameter
import com.maksimowiczm.foodyou.core.feature.addfood.ui.rememberAddFoodState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.searchbar.AddFoodSearchBar
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.searchbar.rememberSearchBarState
import com.maksimowiczm.foodyou.core.ui.component.LoadingIndicator
import com.maksimowiczm.foodyou.core.ui.preview.SharedTransitionPreview
import kotlinx.coroutines.CancellationException
import java.time.LocalDateTime
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    addFoodState: AddFoodState,
    onSearchSettings: () -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onProductClick: (ProductSearchUiModel) -> Unit,
    onCreateProduct: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

    val isEmpty by remember(addFoodState.searchListState) {
        derivedStateOf {
            addFoodState.searchListState.products.isEmpty() && !addFoodState.searchListState.isLoading
        }
    }

    // TODO
    // Do some nice predictive back handler for query
    PredictiveBackHandler(
        enabled = addFoodState.searchBarState.textFieldState.text.isNotEmpty()
    ) { flow ->
        try {
            flow.collect {}
            addFoodState.searchBarState.textFieldState.clearText()
            onClearSearch()
        } catch (_: CancellationException) {
        }
    }

    // Make sure that bottom bar is visible when user can't scroll
    LaunchedEffect(isEmpty) {
        if (isEmpty) {
            bottomAppBarScrollBehavior.nestedScrollConnection.onPostScroll(
                consumed = Offset.Infinite,
                available = Offset.Infinite,
                source = NestedScrollSource.UserInput
            )
            bottomAppBarScrollBehavior.nestedScrollConnection.onPostFling(
                consumed = Velocity.Zero,
                available = Velocity.Zero
            )
        }
    }

    Surface(modifier) {
        SearchHomeLayout(
            animatedVisibilityScope = animatedVisibilityScope,
            addFoodState = addFoodState,
            onCreateProduct = onCreateProduct,
            onProductClick = onProductClick,
            onBack = onBack,
            onSearchSettings = onSearchSettings,
            onSearch = onSearch,
            onClearSearch = onClearSearch,
            onRetry = onRetry,
            onBarcodeScanner = onBarcodeScanner,
            scrollBehavior = bottomAppBarScrollBehavior
        )
    }
}

enum class ErrorCardState {
    HIDDEN_START,
    VISIBLE,
    HIDDEN_END
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchHomeLayout(
    animatedVisibilityScope: AnimatedVisibilityScope,
    addFoodState: AddFoodState,
    onCreateProduct: () -> Unit,
    onProductClick: (ProductSearchUiModel) -> Unit,
    onBack: () -> Unit,
    onSearchSettings: () -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onRetry: () -> Unit,
    onBarcodeScanner: () -> Unit,
    scrollBehavior: BottomAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var searchBarHeightDp by remember { mutableStateOf(0.dp) }

    val searchBar = @Composable {
        AddFoodSearchBar(
            searchBarState = addFoodState.searchBarState,
            onSearchSettings = onSearchSettings,
            onSearch = onSearch,
            onClearSearch = onClearSearch,
            onBack = onBack,
            modifier = Modifier.onSizeChanged {
                searchBarHeightDp = density.run { it.height.toDp() }
            }
        )
    }

    var errorCardHeightPx by remember { mutableIntStateOf(0) }
    val errorCardHeight by remember(errorCardHeightPx) { derivedStateOf { density.run { errorCardHeightPx.toDp() } } }
    val isError by remember { derivedStateOf { addFoodState.searchListState.isError } }
    val anchoredDraggableState = rememberSaveable(
        isError,
        saver = AnchoredDraggableState.Saver()
    ) {
        AnchoredDraggableState(
            initialValue = if (isError) ErrorCardState.VISIBLE else ErrorCardState.HIDDEN_END
        )
    }
    val errorCard = @Composable {
        AnimatedVisibility(
            visible = isError && anchoredDraggableState.settledValue == ErrorCardState.VISIBLE,
            modifier = Modifier
                .windowInsetsPadding(
                    WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                )
                .fillMaxWidth()
                .zIndex(1f)
                .onSizeChanged {
                    val draggableAnchors = DraggableAnchors {
                        ErrorCardState.HIDDEN_START at -it.width.toFloat()
                        ErrorCardState.VISIBLE at 0f
                        ErrorCardState.HIDDEN_END at it.width.toFloat()
                    }

                    anchoredDraggableState.updateAnchors(draggableAnchors)

                    errorCardHeightPx = it.height
                }
                .anchoredDraggable(
                    state = anchoredDraggableState,
                    orientation = Orientation.Horizontal
                )
                .offset {
                    val x = anchoredDraggableState.offset.roundToInt()
                    IntOffset(x, 0)
                }
        ) {
            FoodDatabaseErrorCard(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
                onRetry = onRetry
            )
        }
    }

    // Put them both in column because there is wierd behaviour when they are placed in layout.
    // - Black bar appears between list and bottom bar when bottom bar height changes.
    val listWithBar = @Composable {
        Column(
            modifier = Modifier.testTag("Content")
        ) {
            ProductsLazyColumn(
                searchListState = addFoodState.searchListState,
                onProductClick = onProductClick,
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topOffset = {
                    item {
                        Spacer(
                            Modifier.height(
                                searchBarHeightDp + errorCardHeight + 8.dp
                            )
                        )
                    }
                },
                bottomOffset = {
                    // Don't do system bars padding if the bottom app bar is visible
                    item {
                        val bottomInset =
                            WindowInsets.systemBars.getBottom(LocalDensity.current)
                        val height =
                            bottomInset * (scrollBehavior.state.collapsedFraction)

                        Spacer(
                            Modifier.height(LocalDensity.current.run { height.toDp() })
                        )
                    }
                }
            )
            SearchBottomBar(
                animatedVisibilityScope = animatedVisibilityScope,
                state = addFoodState.searchBottomBarState,
                onCreateProduct = onCreateProduct,
                onBarcodeScanner = onBarcodeScanner,
                scrollBehavior = scrollBehavior
            )
        }
    }

    val empty = @Composable {
        if (addFoodState.searchListState.products.isEmpty() && !addFoodState.searchListState.isLoading) {
            Text(
                text = stringResource(R.string.neutral_no_products_found)
            )
        }
    }

    Layout(
        contents = listOf(
            searchBar,
            errorCard,
            listWithBar,
            empty
        ),
        modifier = modifier
    ) { (searchBar, errorCard, listWithBar, empty), constraints ->
        val searchBarPlaceable = searchBar.first().measure(constraints)
        val errorCardPlaceable = errorCard.firstOrNull()?.measure(constraints)
        val listWithBarPlaceable = listWithBar.first().measure(constraints)
        val emptyPlaceable = empty.firstOrNull()?.measure(constraints)

        layout(constraints.maxWidth, constraints.maxHeight) {
            // Search bar - center top
            val searchBarX = (constraints.maxWidth - searchBarPlaceable.width) / 2
            searchBarPlaceable.place(searchBarX, 0)

            // Error card - under search bar
            errorCardPlaceable?.place(0, searchBarPlaceable.height)

            // List with bottom bar - everywhere
            listWithBarPlaceable.place(0, 0)

            // Empty - center
            emptyPlaceable?.place(
                (constraints.maxWidth - emptyPlaceable.width) / 2,
                (constraints.maxHeight - emptyPlaceable.height) / 2
            )
        }
    }
}

@Composable
private fun ProductsLazyColumn(
    searchListState: SearchListState,
    onProductClick: (ProductSearchUiModel) -> Unit,
    modifier: Modifier = Modifier,
    topOffset: LazyListScope.() -> Unit = {},
    bottomOffset: LazyListScope.() -> Unit = {}
) {
    Box(modifier) {
        if (searchListState.isLoading) {
            LoadingIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Top))
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                    // Searchbar padding
                    .padding(top = 8.dp)
                    // Searchbar height
                    .padding(top = 56.dp)
                    // Padding
                    .padding(top = 16.dp)
                    .zIndex(1f)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = searchListState.lazyListState
        ) {
            topOffset()

            itemsIndexed(
                items = searchListState.products
            ) { index, product ->
                ProductSearchListItem(
                    uiModel = product,
                    onClick = {
                        onProductClick(product)
                    },
                    onCheckChange = {
                        searchListState.onProductCheckChange(
                            index = index,
                            checked = it
                        )
                    },
                    colors = ProductSearchListItemDefaults.colors(
                        checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        checkedToggleButtonContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        checkedToggleButtonContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )
            }

            bottomOffset()
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun SearchHomePreview() {
    val products = ProductSearchUiModelPreviewParameter().values.toList()

    SharedTransitionPreview { _, animatedVisibilityScope ->
        SearchHome(
            animatedVisibilityScope = animatedVisibilityScope,
            addFoodState = rememberAddFoodState(
                searchListState = rememberSearchListState(
                    initialProducts = products
                ),
                searchBottomBarState = rememberSearchBottomBarState(
                    totalCalories = products.filter { it.isChecked }.sumOf { it.model.calories }
                )
            ),
            onSearchSettings = {},
            onSearch = {},
            onClearSearch = {},
            onRetry = {},
            onBack = {},
            onProductClick = {},
            onCreateProduct = {},
            onBarcodeScanner = {}
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun SearchHomePreview2() {
    val products = ProductSearchUiModelPreviewParameter().values.toList()

    SharedTransitionPreview { _, animatedVisibilityScope ->
        SearchHome(
            animatedVisibilityScope = animatedVisibilityScope,
            addFoodState = rememberAddFoodState(
                searchBarState = rememberSearchBarState(
                    initialExpanded = true,
                    initialRecentQueries = listOf(
                        ProductQuery("Banana", LocalDateTime.now()),
                        ProductQuery("Apple", LocalDateTime.now()),
                        ProductQuery("Orange", LocalDateTime.now())
                    )
                ),
                searchListState = rememberSearchListState(
                    initialProducts = products
                ),
                searchBottomBarState = rememberSearchBottomBarState(
                    totalCalories = products.filter { it.isChecked }.sumOf { it.model.calories }
                )
            ),
            onSearchSettings = {},
            onSearch = {},
            onClearSearch = {},
            onRetry = {},
            onBack = {},
            onProductClick = {},
            onCreateProduct = {},
            onBarcodeScanner = {}
        )
    }
}
