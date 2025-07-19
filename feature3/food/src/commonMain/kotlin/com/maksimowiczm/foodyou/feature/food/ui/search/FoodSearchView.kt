package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.ui.Icon
import com.maksimowiczm.foodyou.feature.food.ui.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FoodSearchView(
    availableSources: List<FoodSource.Type>,
    source: FoodSource.Type,
    recentSearches: List<String>,
    onSource: (FoodSource.Type) -> Unit,
    onFill: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(recentSearches.take(3)) {
            FoodSearchItem(
                search = it,
                onFill = { onFill(it) },
                modifier = Modifier.clickable { onSearch(it) }
            )
        }

        if (availableSources.size > 1) {
            item {
                if (recentSearches.isNotEmpty()) {
                    HorizontalDivider()
                }

                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.headline_filters),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(availableSources) {
                            DatabaseFilterIconButton(
                                selected = source == it,
                                onClick = { onSource(it) },
                                logo = { it.Icon() },
                                label = {
                                    Text(
                                        text = it.stringResource(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    }
                }

                if (recentSearches.size > 3) {
                    HorizontalDivider()
                }
            }
        }

        items(recentSearches.drop(3)) {
            FoodSearchItem(
                search = it,
                onFill = { onFill(it) },
                modifier = Modifier.clickable { onSearch(it) }
            )
        }
    }
}

@Composable
private fun FoodSearchItem(search: String, onFill: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(search) },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null
            )
        },
        trailingContent = {
            IconButton(onFill) {
                Icon(
                    imageVector = Icons.Outlined.NorthWest,
                    contentDescription = stringResource(Res.string.action_insert_suggested_search)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}
