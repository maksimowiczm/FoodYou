package com.maksimowiczm.foodyou.capabilities.foodbrowsing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.maksimowiczm.foodyou.shared.ui.extension.rememberDebounceIsIdle
import foodyou.app.generated.resources.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchFilters(
    collections: List<CollectionFilter>,
    onCollection: (SearchCollection?) -> Unit,
    selected: SearchCollection?,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val rows = if (selected == null) 2 else 1
    val filteredCollections = collections.filter {
        if (selected != null) it.collection::class == selected::class else true
    }

    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(rows),
        contentPadding = contentPadding,
        horizontalItemSpacing = 4.dp,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.padding(bottom = 8.dp).height(if (selected == null) 76.dp else 36.dp),
    ) {
        items(items = filteredCollections, key = { it.collection.toString() }) {
            val isSelected = selected != null && it.collection::class == selected::class
            val colors =
                if (it.state is CollectionFilterState.Error)
                    FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        labelColor = MaterialTheme.colorScheme.onErrorContainer,
                        iconColor = MaterialTheme.colorScheme.onErrorContainer,
                        selectedTrailingIconColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                else
                    FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconColor = MaterialTheme.colorScheme.onSurface,
                    )
            val border =
                FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor =
                        if (it.state is CollectionFilterState.Error) Color.Transparent
                        else MaterialTheme.colorScheme.outlineVariant,
                )

            Box(
                modifier = Modifier.animateItem().height(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) onCollection(it.collection) else onCollection(null)
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(it.collection.stringResource())
                            if (it.state is CollectionFilterState.Loaded) {
                                val count =
                                    remember(it.state.count) {
                                        val count = it.state.count
                                        if (count > 1000) "1000+" else count.toString()
                                    }
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = count,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    },
                    shapes = FilterChipDefaults.shapes(),
                    modifier = Modifier.height(32.dp),
                    leadingIcon = {
                        it.collection.Icon(
                            selected = isSelected,
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    },
                    trailingIcon = {
                        if (isSelected)
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = stringResource(Res.string.action_clear),
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                            )
                        else if (it.state is CollectionFilterState.Error)
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                            )
                        else if (it.state is CollectionFilterState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(FilterChipDefaults.IconSize - 4.dp),
                                strokeWidth = 2.dp,
                            )
                        }
                    },
                    colors = colors,
                    border = border,
                )
            }
        }
        if (selected != null) with(selected) { suffixFilters(onCollection) }
    }
}

@Immutable
@Serializable
data class CollectionFilter(
    val collection: SearchCollection,
    val state: CollectionFilterState,
)

@Composable
fun rememberCollectionFilter(
    collection: SearchCollection,
    count: Int?,
    pages: LazyPagingItems<*>,
): CollectionFilter {
    val isIdle = pages.rememberDebounceIsIdle()
    val hasError = pages.loadState.hasError

    return remember(count, isIdle, hasError) {
        val state =
            when {
                hasError -> CollectionFilterState.Error(count)
                count != null && isIdle -> CollectionFilterState.Loaded(count)
                else -> CollectionFilterState.Loading
            }
        CollectionFilter(collection, state)
    }
}
