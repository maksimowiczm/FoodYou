package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
internal sealed interface DatabaseFilterChipState {
    data object Loading : DatabaseFilterChipState
    data object ActionRequired : DatabaseFilterChipState
    data class Loaded(val count: Int) : DatabaseFilterChipState
}

@Composable
internal fun DatabaseFilterChip(
    state: DatabaseFilterChipState,
    selected: Boolean,
    onClick: () -> Unit,
    logo: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when (state) {
        DatabaseFilterChipState.ActionRequired -> Color.Transparent
        is DatabaseFilterChipState.Loaded,
        DatabaseFilterChipState.Loading -> if (selected) {
            Color.Transparent
        } else {
            MaterialTheme.colorScheme.outlineVariant
        }
    }

    val borderWidth = when (state) {
        DatabaseFilterChipState.ActionRequired -> 0.dp
        is DatabaseFilterChipState.Loaded,
        DatabaseFilterChipState.Loading -> if (selected) {
            0.dp
        } else {
            1.dp
        }
    }

    val color = when (state) {
        DatabaseFilterChipState.ActionRequired -> MaterialTheme.colorScheme.errorContainer
        is DatabaseFilterChipState.Loaded,
        DatabaseFilterChipState.Loading -> if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        }
    }

    val contentColor = when (state) {
        DatabaseFilterChipState.ActionRequired -> MaterialTheme.colorScheme.onErrorContainer
        is DatabaseFilterChipState.Loaded,
        DatabaseFilterChipState.Loading -> if (selected) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = color,
        contentColor = contentColor,
        border = BorderStroke(borderWidth, borderColor),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            logo()
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.labelLarge
            ) {
                label()
            }
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodySmall
            ) {
                when (state) {
                    DatabaseFilterChipState.ActionRequired -> Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null
                    )

                    is DatabaseFilterChipState.Loaded -> Text("${state.count}")

                    DatabaseFilterChipState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.size(DatabaseFilterChipDefaults.logoSize),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

internal object DatabaseFilterChipDefaults {
    val logoSize: Dp = 18.dp
}
