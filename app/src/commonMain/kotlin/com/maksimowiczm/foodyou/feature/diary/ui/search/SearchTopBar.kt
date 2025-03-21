package com.maksimowiczm.foodyou.feature.diary.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import foodyou.app.generated.resources.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    state: SearchTopBarState,
    onBarcodeScanner: () -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val onSearchInternal: (String) -> Unit = {
        state.textFieldState.setTextAndPlaceCursorAtEnd(it)
        onSearch(it)
        scope.launch { state.searchBarState.animateToCollapsed() }
    }

    val onClearInternal = {
        state.textFieldState.setTextAndPlaceCursorAtEnd("")
        onClearSearch()
    }

    TopSearchBar(
        state = state.searchBarState,
        inputField = {
            InputField(
                textFieldState = state.textFieldState,
                state = state,
                onSearch = onSearchInternal,
                onClear = onClearInternal,
                onBarcodeScanner = onBarcodeScanner,
                onBack = onBack,
                scope = scope,
                modifier = Modifier.testTag("CollapsedSearchBarInput")
            )
        },
        modifier = modifier
    )

    ExpandedFullScreenSearchBar(
        state = state.searchBarState,
        inputField = {
            InputField(
                textFieldState = state.textFieldState,
                state = state,
                onSearch = onSearchInternal,
                onClear = onClearInternal,
                onBarcodeScanner = onBarcodeScanner,
                onBack = onBack,
                scope = scope,
                modifier = Modifier.testTag("ExpandedSearchBarInput")
            )
        }
    ) {
        SearchResults(
            state = state,
            onSearch = onSearchInternal,
            onQueryClick = { state.textFieldState.setTextAndPlaceCursorAtEnd(it.query) },
            modifier = Modifier.testTag("SearchResults")
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputField(
    textFieldState: TextFieldState,
    state: SearchTopBarState,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onBarcodeScanner: () -> Unit,
    onBack: () -> Unit,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    SearchBarDefaults.InputField(
        searchBarState = state.searchBarState,
        textFieldState = textFieldState,
        onSearch = onSearch,
        modifier = modifier,
        placeholder = { Text(stringResource(Res.string.action_search)) },
        leadingIcon = {
            IconButton(
                onClick = {
                    if (state.searchBarState.currentValue == SearchBarValue.Expanded) {
                        scope.launch { state.searchBarState.animateToCollapsed() }
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
                if (textFieldState.text.isNotBlank()) {
                    IconButton(
                        onClick = onClear
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

@Composable
private fun SearchResults(
    state: SearchTopBarState,
    onSearch: (String) -> Unit,
    onQueryClick: (ProductQuery) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(state.recentQueries) { productQuery ->
            ListItem(
                modifier = Modifier.clickable { onSearch(productQuery.query) },
                headlineContent = {
                    Text(
                        text = productQuery.query
                    )
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
                        onClick = { onQueryClick(productQuery) }
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
