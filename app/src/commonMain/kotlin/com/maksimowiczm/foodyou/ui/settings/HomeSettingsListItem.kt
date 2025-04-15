package com.maksimowiczm.foodyou.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(Res.string.headline_home)
            )
        },
        modifier = modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = null
            )
        },
        supportingContent = {
            Text(
                text = stringResource(Res.string.action_edit_your_home_page)
            )
        }
    )
}
