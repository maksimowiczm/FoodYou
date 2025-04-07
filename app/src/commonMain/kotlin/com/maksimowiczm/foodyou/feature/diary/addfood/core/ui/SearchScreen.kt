package com.maksimowiczm.foodyou.feature.diary.addfood.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.maksimowiczm.foodyou.core.ui.ext.plus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    pages: LazyPagingItems<*>,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onBack: (() -> Unit)?,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState = rememberTextFieldState(),
    searchBarState: SearchBarState = rememberSearchBarState(SearchBarValue.Collapsed),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    topBar: (@Composable () -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    fullScreenSearchBarContent: @Composable () -> Unit = {},
    errorCard: (@Composable () -> Unit)? = null,
    hintCard: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.(PaddingValues) -> Unit
) {
    val isLoading by remember(pages.loadState) {
        derivedStateOf {
            pages.loadState.refresh == LoadState.Loading ||
                pages.loadState.append == LoadState.Loading
        }
    }
    val hasError by remember(pages.loadState) {
        derivedStateOf { pages.loadState.hasError }
    }

    var subSearchBarHeight by remember { mutableIntStateOf(0) }
    val subSearchBar = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { subSearchBarHeight = it.height },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (errorCard != null) {
                DraggableVisibility(
                    initialValue = if (hasError) CardState.VISIBLE else CardState.HIDDEN_END
                ) {
                    errorCard()
                }
            }

            if (hintCard != null) {
                DraggableVisibility {
                    hintCard()
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    val inputField = @Composable {
        SearchBarInputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            onSearch = {
                onSearch(it)
                coroutineScope.launch {
                    searchBarState.animateToCollapsed()
                }
            },
            onBack = onBack?.let {
                {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        coroutineScope.launch {
                            searchBarState.animateToCollapsed()
                        }
                    } else {
                        onBack()
                    }
                }
            },
            onClear = {
                textFieldState.clearText()
                onClear()
            },
            onBarcodeScanner = onBarcodeScanner
        )
    }

    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField
    ) {
        fullScreenSearchBarContent()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            when (topBar) {
                null -> TopSearchBar(
                    state = searchBarState,
                    inputField = inputField
                )

                else -> Column {
                    topBar()

                    val topInsets = SearchBarDefaults.windowInsets.only(WindowInsetsSides.Top)
                    TopSearchBar(
                        state = searchBarState,
                        inputField = inputField,
                        windowInsets = SearchBarDefaults.windowInsets.exclude(topInsets)
                    )
                }
            }
        },
        floatingActionButton = floatingActionButton
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

            content(
                paddingValues + PaddingValues(
                    top = LocalDensity.current.run { subSearchBarHeight.toDp() }
                )
            )
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
