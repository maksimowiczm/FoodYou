package com.maksimowiczm.foodyou.feature.goals

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.core.ui.component.SettingsListItem
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_daily_goals
import foodyou.app.generated.resources.neutral_set_your_daily_goals
import org.jetbrains.compose.resources.stringResource

@Composable
fun GoalsSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    SettingsListItem(
        headlineContent = {
            Text(
                text = stringResource(Res.string.headline_daily_goals)
            )
        },
        onClick = onClick,
        modifier = modifier,
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Flag,
                contentDescription = null
            )
        },
        supportingContent = {
            Text(
                text = stringResource(Res.string.neutral_set_your_daily_goals)
            )
        },
        containerColor = containerColor,
        contentColor = contentColor
    )
}
