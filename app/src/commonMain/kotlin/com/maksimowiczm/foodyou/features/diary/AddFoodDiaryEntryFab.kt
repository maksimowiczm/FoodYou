package com.maksimowiczm.foodyou.features.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddFoodDiaryEntryFab(
    isTracked: Boolean,
    onIsTrackedChange: (Boolean) -> Unit,
    onAdd: (trackFood: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isTrackingEnabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TooltipBox(
            positionProvider =
                TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = {
                RichTooltip(title = { Text(stringResource(Res.string.headline_diary_sync)) }) {
                    Text(stringResource(Res.string.description_diary_sync))
                }
            },
            state = rememberTooltipState(isPersistent = true),
        ) {
            IconToggleButton(
                checked = isTracked,
                onCheckedChange = onIsTrackedChange,
                enabled = isTrackingEnabled,
                shapes = IconButtonDefaults.toggleableShapes(),
                colors =
                    IconButtonDefaults.iconToggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        checkedContainerColor = MaterialTheme.colorScheme.secondary,
                        checkedContentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                modifier = Modifier.size(IconButtonDefaults.mediumContainerSize()),
            ) {
                if (isTracked)
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize),
                    )
                else
                    Icon(
                        imageVector = Icons.Outlined.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize),
                    )
            }
        }
        LargeExtendedFloatingActionButton(
            onClick = {
                onAdd(isTracked)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
            )
            Spacer(Modifier.width(16.dp))
            Text(stringResource(Res.string.action_save))
        }
        Spacer(Modifier.size(IconButtonDefaults.mediumContainerSize()))
    }
}
