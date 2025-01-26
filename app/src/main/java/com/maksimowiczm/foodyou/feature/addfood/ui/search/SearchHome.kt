package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.QueryResult
import com.maksimowiczm.foodyou.feature.addfood.ui.previewparameter.ProductWithWeightMeasurementPreviewParameter
import com.maksimowiczm.foodyou.ui.component.LoadingIndicator
import com.maksimowiczm.foodyou.ui.preview.SharedTransitionPreview
import com.maksimowiczm.foodyou.ui.preview.asList
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHome(
    animatedVisibilityScope: AnimatedVisibilityScope,
    totalCalories: Int,
    onCreateProduct: () -> Unit,
    onProductClick: (ProductSearchUiModel) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    searchState: SearchState = rememberSearchState()
) {
    val bottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

    val isEmpty by remember(searchState) { derivedStateOf { searchState.products.isEmpty() && !searchState.isLoading } }

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
            totalCalories = totalCalories,
            onCreateProduct = onCreateProduct,
            onProductClick = onProductClick,
            onBack = onBack,
            searchState = searchState,
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
    searchState: SearchState,
    totalCalories: Int,
    onCreateProduct: () -> Unit,
    onProductClick: (ProductSearchUiModel) -> Unit,
    onBack: () -> Unit,
    scrollBehavior: BottomAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    val topInsets = WindowInsets.displayCutout.union(WindowInsets.systemBars)
    val searchBarOffset =
        topInsets.asPaddingValues()
            .calculateTopPadding() + SearchBarDefaults.InputFieldHeight + 16.dp

    val searchBar = @Composable {
        MySearchBar(
            searchState = searchState,
            onBack = onBack
        )
    }

    val density = LocalDensity.current
    var errorCardHeightPx by remember { mutableIntStateOf(0) }
    val errorCardHeight by remember(errorCardHeightPx) { derivedStateOf { density.run { errorCardHeightPx.toDp() } } }
    val anchoredDraggableState = rememberSaveable(
        searchState.isError,
        saver = AnchoredDraggableState.Saver()
    ) {
        AnchoredDraggableState(
            initialValue = if (searchState.isError) ErrorCardState.VISIBLE else ErrorCardState.HIDDEN_END
        )
    }
    val errorCard = @Composable {
        AnimatedVisibility(
            visible = searchState.isError && anchoredDraggableState.settledValue == ErrorCardState.VISIBLE,
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
                onRetry = searchState::onRetry
            )
        }
    }

    // Put them both in column because there is wierd behaviour when they are placed in layout.
    // - Black bar between list and bottom bar when bottom bar changes height.
    val listWithBar = @Composable {
        Column {
            ProductsLazyColumn(
                searchState = searchState,
                onProductClick = onProductClick,
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topOffset = {
                    item {
                        Spacer(Modifier.height(searchBarOffset + errorCardHeight))
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
                searchState = searchState,
                totalCalories = totalCalories,
                onCreateProduct = onCreateProduct,
                scrollBehavior = scrollBehavior
            )
        }
    }

    val empty = @Composable {
        if (searchState.products.isEmpty() && !searchState.isLoading) {
            Text(
                text = stringResource(R.string.feedback_no_products_found)
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
    searchState: SearchState,
    onProductClick: (ProductSearchUiModel) -> Unit,
    modifier: Modifier = Modifier,
    topOffset: LazyListScope.() -> Unit = {},
    bottomOffset: LazyListScope.() -> Unit = {}
) {
    Box(modifier) {
        if (searchState.isLoading) {
            LoadingIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(
                        WindowInsets.displayCutout.only(WindowInsetsSides.Top)
                    )
                    .windowInsetsPadding(
                        WindowInsets.systemBars.only(WindowInsetsSides.Top)
                    )
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
            state = searchState.lazyListState
        ) {
            topOffset()

            itemsIndexed(
                items = searchState.products,
                key = { _, product -> "${product.model.measurementId}-${product.model.product.id}" }
            ) { index, product ->
                ProductSearchListItem(
                    uiModel = product,
                    onClick = {
                        onProductClick(product)
                    },
                    onCheckChange = {
                        searchState.onProductCheckChange(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MySearchBar(
    searchState: SearchState,
    onBack: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState(
        initialText = searchState.query,
        initialSelection = TextRange(searchState.query.length)
    )
    val onSearch: (String) -> Unit = {
        searchState.onSearch(it)
        textFieldState.setTextAndPlaceCursorAtEnd(it)
        expanded = false
    }

    LaunchedEffect(expanded) {
        if (!expanded) {
            if (searchState.query != textFieldState.text) {
                textFieldState.setTextAndPlaceCursorAtEnd(searchState.query)
            }
        }
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                state = textFieldState,
                onSearch = onSearch,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = {
                    Text(stringResource(R.string.action_search))
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            if (expanded) {
                                expanded = false
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_go_back)
                        )
                    }
                },
                trailingIcon = {
                    if (textFieldState.text.isNotBlank()) {
                        IconButton(
                            onClick = {
                                if (expanded) {
                                    textFieldState.clearText()
                                } else {
                                    onSearch("")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.action_clear)
                            )
                        }
                    }
                }
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        val recentQueries = searchState.recentQueries

        LazyColumn {
            items(recentQueries) { productQuery ->
                ListItem(
                    modifier = Modifier.clickable { onSearch(productQuery.query) },
                    headlineContent = {
                        Text(
                            modifier = Modifier.displayCutoutPadding(),
                            text = productQuery.query
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    leadingContent = {
                        Icon(
                            modifier = Modifier.displayCutoutPadding(),
                            painter = painterResource(R.drawable.ic_schedule_24),
                            contentDescription = stringResource(R.string.action_search)
                        )
                    },
                    trailingContent = {
                        IconButton(
                            modifier = Modifier.displayCutoutPadding(),
                            onClick = {
                                textFieldState.setTextAndPlaceCursorAtEnd(productQuery.query)
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_north_west_24),
                                contentDescription = stringResource(R.string.action_search)
                            )
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    showSystemUi = true
)
@Composable
private fun SearchHomePreview() {
    val products = ProductWithWeightMeasurementPreviewParameter().asList()

    FoodYouTheme {
        SharedTransitionPreview { _, animatedVisibilityScope ->
            SearchHome(
                animatedVisibilityScope = animatedVisibilityScope,
                totalCalories = 678,
                onCreateProduct = {},
                onProductClick = {},
                onBack = {},
                searchState = rememberSearchState(
                    initialQueryResult = QueryResult(
                        isLoading = true,
                        error = Error(),
                        data = products
                    )
                )
            )
        }
    }
}
