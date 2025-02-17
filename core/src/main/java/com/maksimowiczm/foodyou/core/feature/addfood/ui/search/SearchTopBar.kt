package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.maksimowiczm.foodyou.core.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    state: SearchTopBarState,
    onSearchSettings: () -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val onSearchInternal: (String) -> Unit = {
        onSearch(it)
        state.textFieldState.setTextAndPlaceCursorAtEnd(it)
        scope.launch { state.searchBarState.animateToCollapsed() }
    }

    val onClearInternal: () -> Unit = {
        onClearSearch()
        state.textFieldState.clearText()
    }

    TopSearchBar(
        state = state.searchBarState,
        inputField = {
            InputField(
                state = state,
                onSearchInternal = onSearchInternal,
                onClearInternal = onClearInternal,
                onSearchSettings = onSearchSettings,
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
                state = state,
                onSearchInternal = onSearchInternal,
                onClearInternal = onClearInternal,
                onSearchSettings = onSearchSettings,
                onBack = onBack,
                scope = scope,
                modifier = Modifier.testTag("ExpandedSearchBarInput")
            )
        }
    ) {
        SearchResults(
            state = state,
            onSearch = onSearchInternal,
            modifier = Modifier.testTag("SearchResults")
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputField(
    state: SearchTopBarState,
    onSearchInternal: (String) -> Unit,
    onClearInternal: () -> Unit,
    onSearchSettings: () -> Unit,
    onBack: () -> Unit,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    SearchBarDefaults.InputField(
        searchBarState = state.searchBarState,
        textFieldState = state.textFieldState,
        onSearch = onSearchInternal,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.action_search)) },
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
                    contentDescription = stringResource(R.string.action_go_back)
                )
            }
        },
        trailingIcon = {
            if (state.textFieldState.text.isNotBlank()) {
                IconButton(
                    onClick = {
                        if (state.searchBarState.currentValue == SearchBarValue.Expanded) {
                            state.textFieldState.clearText()
                        } else {
                            onClearInternal()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.action_clear)
                    )
                }
            } else if (state.searchBarState.currentValue == SearchBarValue.Collapsed) {
                IconButton(
                    onClick = onSearchSettings
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.action_open_settings)
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
                        painter = painterResource(R.drawable.ic_schedule_24),
                        contentDescription = stringResource(R.string.action_search)
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            state.textFieldState.setTextAndPlaceCursorAtEnd(productQuery.query)
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
