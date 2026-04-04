package com.maksimowiczm.foodyou.app.ui.home.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchFilterChips(
    selectedCollection: CollectionFilter,
    onSelectCollection: (CollectionFilter) -> Unit,
    collections: List<CollectionFilter>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            var showCollectionSelection by rememberSaveable { mutableStateOf(false) }
            FilterChip(
                selected = true,
                onClick = { showCollectionSelection = true },
                label = { Text(selectedCollection.stringResource()) },
                leadingIcon = {
                    selectedCollection.Icon(Modifier.size(FilterChipDefaults.IconSize))
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = stringResource(Res.string.action_choose_collection),
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                },
            )
            DropdownMenu(
                expanded = showCollectionSelection,
                onDismissRequest = { showCollectionSelection = false },
            ) {
                collections.forEach {
                    DropdownMenuItem(
                        text = { Text(it.stringResource()) },
                        onClick = {
                            onSelectCollection(it)
                            showCollectionSelection = false
                        },
                        leadingIcon = { it.Icon(Modifier.size(FilterChipDefaults.IconSize)) },
                    )
                }
            }
        }
    }
}
