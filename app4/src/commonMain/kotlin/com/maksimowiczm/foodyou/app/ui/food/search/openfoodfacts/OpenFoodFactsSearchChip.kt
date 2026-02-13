package com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.app.ui.common.extension.debounceIsIdle
import com.maksimowiczm.foodyou.app.ui.food.search.FoodFilter
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun OpenFoodFactsSearchChip(
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OpenFoodFactsSearchViewModel = koinViewModel(),
) {
    val count = viewModel.count.collectAsStateWithLifecycle().value
    val shouldShow = viewModel.shouldShowFilter.collectAsStateWithLifecycle().value
    if (!shouldShow) {
        return
    }

    val pages = viewModel.pages.collectAsLazyPagingItems()
    val isIdle = remember(pages) { pages.debounceIsIdle() }.collectAsStateWithLifecycle(false).value
    val hasError = pages.loadState.hasError

    val colors =
        if (hasError) {
            FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                labelColor = MaterialTheme.colorScheme.onErrorContainer,
                iconColor = MaterialTheme.colorScheme.onErrorContainer,
                selectedContainerColor = MaterialTheme.colorScheme.error,
                selectedLabelColor = MaterialTheme.colorScheme.onError,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onError,
                selectedTrailingIconColor = MaterialTheme.colorScheme.onError,
            )
        } else {
            FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface)
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
        onClick = onSelect,
        label = { Text(stringResource(Res.string.headline_open_food_facts)) },
        modifier = modifier,
        leadingIcon = {
            FoodFilter.Source.OpenFoodFacts.Icon(Modifier.size(FilterChipDefaults.IconSize))
        },
        trailingIcon = {
            if (hasError) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            } else {
                if (!isIdle || count == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(text = count.toString(), style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        colors = colors,
        border = border,
    )
}
