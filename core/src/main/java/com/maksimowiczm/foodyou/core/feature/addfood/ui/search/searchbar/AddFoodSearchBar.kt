package com.maksimowiczm.foodyou.core.feature.addfood.ui.search.searchbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.ui.preview.BooleanPreviewParameter
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodSearchBar(
    searchBarState: SearchBarState,
    onSearchSettings: () -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null
) {
    val onSearchInternal: (String) -> Unit = {
        onSearch(it)
        searchBarState.textFieldState.setTextAndPlaceCursorAtEnd(it)
        searchBarState.requestExpandedState(false)
    }
    val onClearInternal: () -> Unit = {
        onClearSearch()
        searchBarState.textFieldState.clearText()
    }

    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                state = searchBarState.textFieldState,
                onSearch = onSearchInternal,
                expanded = searchBarState.expanded,
                onExpandedChange = searchBarState::requestExpandedState,
                modifier = Modifier.testTag("SearchBarInput"),
                placeholder = {
                    Text(stringResource(R.string.action_search))
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            if (searchBarState.expanded) {
                                searchBarState.requestExpandedState(false)
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
                    if (searchBarState.textFieldState.text.isNotBlank()) {
                        IconButton(
                            onClick = {
                                if (searchBarState.expanded) {
                                    searchBarState.textFieldState.clearText()
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
                    } else {
                        IconButton(
                            onClick = onSearchSettings
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.action_open_settings)
                            )
                        }
                    }
                },
                interactionSource = interactionSource
            )
        },
        expanded = searchBarState.expanded,
        onExpandedChange = searchBarState::requestExpandedState,
        modifier = modifier
    ) {
        val recentQueries = searchBarState.recentQueries

        LazyColumn(
            modifier = Modifier.testTag("SearchViewContent")
        ) {
            items(recentQueries) { productQuery ->
                ListItem(
                    modifier = Modifier.clickable { onSearchInternal(productQuery.query) },
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
                                searchBarState.textFieldState
                                    .setTextAndPlaceCursorAtEnd(productQuery.query)
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

@Preview
@Composable
private fun AddFoodSearchBarPreview(
    @PreviewParameter(BooleanPreviewParameter::class) expanded: Boolean
) {
    FoodYouTheme {
        AddFoodSearchBar(
            searchBarState = rememberSearchBarState(
                // reverse for better preview order
                initialExpanded = expanded.not()
            ),
            onSearchSettings = {},
            onSearch = {},
            onClearSearch = {},
            onBack = {}
        )
    }
}
