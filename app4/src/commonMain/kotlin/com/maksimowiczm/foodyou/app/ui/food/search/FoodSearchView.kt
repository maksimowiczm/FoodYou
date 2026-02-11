package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodSearchView(
    searchBarState: SearchBarState,
    recentSearches: List<String>,
    selectedFilter: FoodFilter.Source,
    filters: List<FoodFilter.Source>,
    onFill: (String) -> Unit,
    onSearch: (String?) -> Unit,
    onSource: (FoodFilter.Source) -> Unit,
    inputField: @Composable () -> Unit,
) {
    ExpandedFullScreenSearchBar(state = searchBarState, inputField = inputField) {
        LazyColumn {
            items(recentSearches.take(3)) {
                FoodSearchItem(
                    search = it,
                    onFill = { onFill(it) },
                    modifier = Modifier.clickable { onSearch(it) },
                )
            }

            item {
                if (recentSearches.isNotEmpty()) {
                    HorizontalDivider()
                }

                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.headline_filters),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    ) {
                        items(filters.toList()) { source ->
                            DatabaseFilterIconButton(
                                selected = selectedFilter == source,
                                onClick = { onSource(source) },
                                logo = { source.Icon(it) },
                                label = {
                                    Text(
                                        text = source.stringResource(),
                                        textAlign = TextAlign.Center,
                                    )
                                },
                            )
                        }
                    }
                }

                HorizontalDivider()
            }

            items(recentSearches.drop(3)) {
                FoodSearchItem(
                    search = it,
                    onFill = { onFill(it) },
                    modifier = Modifier.clickable { onSearch(it) },
                )
            }
        }
    }
}

@Composable
private fun FoodSearchItem(search: String, onFill: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(search) },
        leadingContent = { Icon(imageVector = Icons.Outlined.History, contentDescription = null) },
        trailingContent = {
            IconButton(onFill) {
                Icon(
                    imageVector = Icons.Outlined.NorthWest,
                    contentDescription = stringResource(Res.string.action_insert_suggested_search),
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}

@Composable
private fun DatabaseFilterIconButton(
    selected: Boolean,
    onClick: () -> Unit,
    logo: @Composable (Modifier) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val containerColor by
        animateColorAsState(
            if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    val contentColor by
        animateColorAsState(
            if (selected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
        )

    val labelColor by
        animateColorAsState(
            if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )

    Column(
        modifier =
            modifier
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    role = Role.RadioButton,
                    interactionSource = interactionSource,
                    indication = null,
                )
                .widthIn(min = 64.dp, max = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            onClick = onClick,
            shapes = IconButtonDefaults.shapes(),
            modifier =
                Modifier.size(IconButtonDefaults.mediumContainerSize()).clearAndSetSemantics {},
            interactionSource = interactionSource,
            colors =
                IconButtonDefaults.iconButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                ),
        ) {
            logo(Modifier.size(IconButtonDefaults.mediumIconSize))
        }
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.labelMedium,
            LocalContentColor provides labelColor,
        ) {
            label()
        }
    }
}
