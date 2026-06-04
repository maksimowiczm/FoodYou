package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfade
import com.maksimowiczm.foodyou.app.ui.common.extension.rememberDebounceIsIdle
import foodyou.app.generated.resources.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchFilters(
    collections: List<CollectionFilter>,
    onCollection: (SearchCollection?) -> Unit,
    selected: SearchCollection?,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    SharedTransitionLayout(modifier) {
        updateTransition(selected).AnimatedContent(
            contentKey = { if (it != null) it::class else "null" },
            modifier = Modifier.fillMaxWidth(),
            transitionSpec = { crossfade() },
        ) { currentSelected ->
            if (currentSelected != null)
                LazyRow(
                    contentPadding = contentPadding,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        FilterChip(
                            selected = true,
                            onClick = { onCollection(null) },
                            label = { Text(currentSelected.stringResource()) },
                            modifier =
                                Modifier
                                    // I have no idea why sharedElement is buggy here, but
                                    // sharedBounds with snap enter and exit transitions work
                                    .sharedBounds(
                                        rememberSharedContentState(
                                            key = currentSelected.toString()
                                        ),
                                        this@AnimatedContent,
                                        enter = fadeIn(snap()),
                                        exit = fadeOut(snap()),
                                    ),
                            leadingIcon = {
                                currentSelected.Icon(
                                    selected = true,
                                    modifier = Modifier.size(AssistChipDefaults.IconSize),
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = stringResource(Res.string.action_clear),
                                    modifier = Modifier.size(AssistChipDefaults.IconSize),
                                )
                            },
                        )
                    }
                    with(currentSelected) { suffixFilters(onCollection) }
                }
            else
                LazyHorizontalStaggeredGrid(
                    rows = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.height((8 + 32 + 8 + 32 + 8).dp).padding(vertical = 8.dp),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalItemSpacing = 8.dp,
                ) {
                    items(collections) {
                        Chip(
                            state = it.state,
                            onClick = { onCollection(it.collection) },
                            label = { Text(it.collection.stringResource()) },
                            leadingIcon = {
                                it.collection.Icon(
                                    selected = false,
                                    modifier = Modifier.size(AssistChipDefaults.IconSize),
                                )
                            },
                            modifier =
                                Modifier.sharedBounds(
                                    rememberSharedContentState(key = it.collection.toString()),
                                    this@AnimatedContent,
                                    enter = fadeIn(snap()),
                                    exit = fadeOut(snap()),
                                ),
                        )
                    }
                }
        }
    }
}

@Composable
private fun Chip(
    state: CollectionFilterState,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors =
        if (state is CollectionFilterState.Error)
            AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                labelColor = MaterialTheme.colorScheme.onErrorContainer,
                leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer,
                trailingIconContentColor = MaterialTheme.colorScheme.onErrorContainer,
            )
        else
            AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surface,
                leadingIconContentColor = MaterialTheme.colorScheme.onSurface,
                trailingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )

    val border =
        AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor =
                if (state is CollectionFilterState.Error) Color.Transparent
                else MaterialTheme.colorScheme.outlineVariant,
        )

    val count =
        remember(state) {
            val count = state.count ?: return@remember null
            if (count > 1000) "1000+" else count.toString()
        }

    AssistChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        colors = colors,
        border = border,
        leadingIcon = leadingIcon,
        trailingIcon = {
            when (state) {
                is CollectionFilterState.Error ->
                    if (count == null || state.count == 0)
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(AssistChipDefaults.IconSize),
                        )
                    else Text(text = count, style = MaterialTheme.typography.bodySmall)

                is CollectionFilterState.Loaded ->
                    Text(text = count!!, style = MaterialTheme.typography.bodySmall)

                CollectionFilterState.Loading ->
                    CircularProgressIndicator(
                        modifier = Modifier.size(AssistChipDefaults.IconSize),
                        strokeWidth = 2.dp,
                    )
            }
        },
    )
}

@Immutable
@Serializable
internal data class CollectionFilter(
    val collection: SearchCollection,
    val state: CollectionFilterState,
)

@Composable
internal fun rememberCollectionFilter(
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
