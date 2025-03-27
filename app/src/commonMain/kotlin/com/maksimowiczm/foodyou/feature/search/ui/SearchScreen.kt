package com.maksimowiczm.foodyou.feature.search.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
import com.maksimowiczm.foodyou.feature.garbage.ui.search.FoodDatabaseErrorCard
import com.maksimowiczm.foodyou.feature.search.domain.Product
import com.maksimowiczm.foodyou.feature.search.domain.ProductQuery
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import foodyou.app.generated.resources.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

typealias SearchScreenItemFactory = () -> (@Composable (Product?) -> Unit)

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
    item: SearchScreenItemFactory
) {
    val pages = viewModel.pages.collectAsLazyPagingItems(
        viewModel.viewModelScope.coroutineContext
    )

    val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()

    SearchScreen(
        pages = pages,
        recentQueries = recentQueries,
        onSearch = viewModel::onSearch,
        onClear = { viewModel.onSearch(null) },
        onBack = onBack,
        onBarcodeScanner = onBarcodeScanner,
        modifier = modifier,
        item = item
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchScreen(
    pages: LazyPagingItems<Product>,
    recentQueries: List<ProductQuery>,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    item: SearchScreenItemFactory
) {
    val searchBarState = rememberSearchBarState(
        initialValue = SearchBarValue.Collapsed
    )
    val textFieldState = rememberTextFieldState()
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

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            onSearch = {
                onSearch(it)
                coroutineScope.launch {
                    searchBarState.animateToCollapsed()
                }
            },
            leadingIcon = {
                IconButton(
                    onClick = {
                        if (searchBarState.currentValue == SearchBarValue.Expanded) {
                            coroutineScope.launch {
                                searchBarState.animateToCollapsed()
                            }
                        } else {
                            onBack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.action_go_back)
                    )
                }
            },
            trailingIcon = {
                Row {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textFieldState.clearText()
                                onClear()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(Res.string.action_clear)
                            )
                        }
                    }

                    IconButton(
                        onClick = onBarcodeScanner
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(Res.string.action_scan_barcode)
                        )
                    }
                }
            }
        )
    }

    var subSearchBarHeight by remember { mutableIntStateOf(0) }
    val subSearchBar = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { subSearchBarHeight = it.height },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DraggableVisibility(
                initialValue = if (hasError) CardState.VISIBLE else CardState.HIDDEN_END
            ) {
                FoodDatabaseErrorCard(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp),
                    onRetry = pages::retry
                )
            }
        }
    }

    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField
    ) {
        LazyColumn {
            items(recentQueries) { (query) ->
                ListItem(
                    modifier = Modifier.clickable {
                        onSearch(query)
                        textFieldState.setTextAndPlaceCursorAtEnd(query)
                        coroutineScope.launch {
                            searchBarState.animateToCollapsed()
                        }
                    },
                    headlineContent = {
                        Text(query)
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = stringResource(Res.string.action_search)
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                textFieldState.setTextAndPlaceCursorAtEnd(query)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NorthWest,
                                contentDescription = stringResource(
                                    Res.string.action_insert_suggested_search
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopSearchBar(
                state = searchBarState,
                inputField = inputField
            )
        }
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
                subSearchBar()

                AnimatedVisibility(
                    visible = isLoading
                ) {
                    LoadingIndicator()
                }
            }

            LazyColumn(
                contentPadding = paddingValues
            ) {
                item {
                    Spacer(Modifier.height(LocalDensity.current.run { subSearchBarHeight.toDp() }))
                }

                if (pages.loadState.refresh == LoadState.Loading && isEmpty) {
                    items(
                        count = 100,
                        key = { "skeleton-refresh-$it" }
                    ) {
                        item()(null)
                    }
                }

                items(
                    count = pages.itemCount,
                    key = pages.itemKey { it.id }
                ) {
                    item()(pages[it])
                }

                if (pages.loadState.append == LoadState.Loading) {
                    items(
                        count = 3,
                        key = { "skeleton-append-$it" }
                    ) {
                        item()(null)
                    }
                }
            }
        }
    }
}

@Composable
private fun DraggableVisibility(
    modifier: Modifier = Modifier,
    initialValue: CardState = CardState.VISIBLE,
    content: @Composable () -> Unit
) {
    val anchoredDraggableState = rememberSaveable(
        initialValue,
        saver = AnchoredDraggableState.Saver()
    ) {
        AnchoredDraggableState(
            initialValue = initialValue
        )
    }

    val density = LocalDensity.current

    BoxWithConstraints {
        SideEffect {
            with(density) {
                val draggableAnchors = DraggableAnchors {
                    CardState.HIDDEN_END at -maxWidth.toPx()
                    CardState.VISIBLE at 0f
                    CardState.HIDDEN_START at maxWidth.toPx()
                }

                anchoredDraggableState.updateAnchors(draggableAnchors)
            }
        }

        AnimatedVisibility(
            visible = anchoredDraggableState.settledValue == CardState.VISIBLE,
            modifier = modifier
                .horizontalDisplayCutoutPadding()
                .fillMaxWidth()
                .anchoredDraggable(
                    state = anchoredDraggableState,
                    orientation = Orientation.Horizontal
                )
                .offset {
                    IntOffset(
                        x = anchoredDraggableState.requireOffset().fastRoundToInt(),
                        y = 0
                    )
                }
        ) {
            content()
        }
    }
}

private enum class CardState {
    HIDDEN_START,
    VISIBLE,
    HIDDEN_END
}
