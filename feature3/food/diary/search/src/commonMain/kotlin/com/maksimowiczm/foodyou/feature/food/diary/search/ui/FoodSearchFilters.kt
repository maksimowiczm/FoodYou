package com.maksimowiczm.foodyou.feature.food.diary.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.food.diary.search.presentation.FoodFilter
import com.maksimowiczm.foodyou.feature.food.diary.search.presentation.FoodSearchUiState
import com.maksimowiczm.foodyou.feature.food.diary.search.presentation.RemoteStatus

@Composable
internal fun FoodSearchFilters(
    uiState: FoodSearchUiState,
    onSource: (FoodFilter.Source) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
) {
    val filters = uiState.sources.filterValues { state -> state.shouldShowFilter }

    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalItemSpacing = 8.dp,
    ) {
        items(filters.toList()) { (source, state) ->
            val pages = state.collectAsLazyPagingItems()
            val isLoading = pages.delayedLoadingState()
            val hasError = pages.loadState.hasError
            val selected = uiState.filter.source == source

            val colors =
                if (hasError) {
                    FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        labelColor = MaterialTheme.colorScheme.onErrorContainer,
                        iconColor = MaterialTheme.colorScheme.onErrorContainer,
                        selectedContainerColor = MaterialTheme.colorScheme.error,
                        selectedLabelColor = MaterialTheme.colorScheme.onError,
                        selectedTrailingIconColor = MaterialTheme.colorScheme.onError,
                    )
                } else {
                    FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }

            val border =
                FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor =
                        if (hasError || selected) {
                            Color.Transparent
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                )

            FilterChip(
                selected = selected,
                onClick = { onSource(source) },
                label = { Text(source.stringResource()) },
                modifier = Modifier.animateItem(),
                leadingIcon = { source.Icon(Modifier.size(FilterChipDefaults.IconSize)) },
                trailingIcon = {
                    if (hasError) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    } else {
                        if (state.remoteEnabled != RemoteStatus.LocalOnly && isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = state.count.toString(),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                },
                colors = colors,
                border = border,
            )
        }
    }
}
