package com.maksimowiczm.foodyou.feature.search.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.feature.search.domain.Product
import com.maksimowiczm.foodyou.feature.search.domain.ProductQuery
import foodyou.app.generated.resources.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
    item: () -> (@Composable (Product?) -> Unit)
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
    item: () -> (@Composable (Product?) -> Unit)
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
            if (isEmpty && pages.loadState.append != LoadState.Loading) {
                Text(
                    modifier = Modifier.align(Alignment.Center).safeContentPadding(),
                    text = stringResource(Res.string.neutral_no_products_found)
                )
            }

            LazyColumn(
                contentPadding = paddingValues
            ) {
                stickyHeader {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AnimatedVisibility(
                            visible = isLoading,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            LoadingIndicator()
                        }
                    }
                }

                items(
                    count = pages.itemCount,
                    key = pages.itemKey { it.id }
                ) {
                    item()(pages[it])
                }
            }
        }
    }
}
