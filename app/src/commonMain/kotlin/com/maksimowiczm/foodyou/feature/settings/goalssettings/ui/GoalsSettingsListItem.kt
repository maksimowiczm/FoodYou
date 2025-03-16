package com.maksimowiczm.foodyou.feature.settings.goalssettings.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GoalsSettingsListItem(onGoalsClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(Res.string.headline_daily_goals)
            )
        },
        modifier = modifier
            .clickable(onClick = onGoalsClick)
            .horizontalDisplayCutoutPadding(),
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
        }
    )
}

@Preview
@Composable
private fun GoalsSettingsListItemPreview() {
    GoalsSettingsListItem(
        onGoalsClick = {}
    )
}
