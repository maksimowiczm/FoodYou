package com.maksimowiczm.foodyou.feature.about.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier.clickable { onClick() },
        headlineContent = {
            Text(
                text = stringResource(Res.string.headline_about)
            )
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null
            )
        },
        supportingContent = {
            Text(
                text = stringResource(Res.string.description_about_setting)
            )
        }
    )
}
