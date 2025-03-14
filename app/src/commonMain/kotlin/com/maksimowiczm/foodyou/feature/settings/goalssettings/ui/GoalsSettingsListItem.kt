package com.maksimowiczm.foodyou.feature.settings.goalssettings.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding

@Composable
fun GoalsSettingsListItem(onGoalsClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.headline_daily_goals)
            )
        },
        modifier = modifier
            .clickable(onClick = onGoalsClick)
            .horizontalDisplayCutoutPadding(),
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_flag_24),
                contentDescription = null
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.neutral_set_your_daily_goals)
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
