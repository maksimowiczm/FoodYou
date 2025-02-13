package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.core.feature.addfood.ui.AddFoodState
import com.maksimowiczm.foodyou.core.feature.addfood.ui.previewparameter.ProductSearchUiModelPreviewParameter
import com.maksimowiczm.foodyou.core.feature.addfood.ui.rememberAddFoodState
import com.maksimowiczm.foodyou.core.ui.component.LoadingIndicator
import com.maksimowiczm.foodyou.core.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.core.ui.preview.SharedTransitionPreview
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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

    val density = LocalDensity.current
    var searchBarHeight by remember { mutableIntStateOf(0) }
    var errorCardHeight by remember { mutableIntStateOf(0) }

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
        .exclude(WindowInsets.systemBars.only(WindowInsetsSides.Top))
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
        topBar = {
            SearchTopBar(
                state = addFoodState.searchTopBarState,
                onSearchSettings = onSearchSettings,
                onSearch = onSearch,
                onClearSearch = onClearSearch,
                onBack = onBack,
                modifier = Modifier.onSizeChanged { searchBarHeight = it.height }
            )
        },
        bottomBar = {
            SearchBottomBar(
                animatedVisibilityScope = animatedVisibilityScope,
                state = addFoodState.searchBottomBarState,
                onCreateProduct = onCreateProduct,
                onBarcodeScanner = onBarcodeScanner,
                scrollBehavior = bottomAppBarScrollBehavior
            )
        },
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isEmpty) {
                Text(
                    text = stringResource(R.string.neutral_no_products_found),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(density.run { searchBarHeight.toDp() }))

                errorCard()

                if (addFoodState.searchListState.isLoading) {
                    Spacer(Modifier.height(8.dp))

                    LoadingIndicator()
                }
            }

            LazyColumn(
                modifier = Modifier.nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection),
                userScrollEnabled = addFoodState.searchListState.products.isNotEmpty(),
                contentPadding = paddingValues
            ) {
                item {
                    Spacer(Modifier.height(density.run { errorCardHeight.toDp() }))
                }

                itemsIndexed(
                    items = addFoodState.searchListState.products
                ) { index, product ->
                    ProductSearchListItem(
                        uiModel = product,
                        onClick = {
                            onProductClick(product)
                        },
                        onCheckChange = {
                            addFoodState.searchListState.onProductCheckChange(
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
                item {
                    // There will be a small gap if bottom bar is expanded but no one will ever notice it :)
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
                }
            }
        }
    }
}

enum class ErrorCardState {
    HIDDEN_START,
    VISIBLE,
    HIDDEN_END
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
                    initialIsLoading = true,
                    initialIsError = true,
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
                searchBarState = rememberSearchTopBarState(
                    initialExpanded = true,
                    initialRecentQueries = listOf(
                        ProductQuery(
                            "Banana",
                            Clock.System.now().toLocalDateTime(TimeZone.UTC)
                        ),
                        ProductQuery("Apple", Clock.System.now().toLocalDateTime(TimeZone.UTC)),
                        ProductQuery("Orange", Clock.System.now().toLocalDateTime(TimeZone.UTC))
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
