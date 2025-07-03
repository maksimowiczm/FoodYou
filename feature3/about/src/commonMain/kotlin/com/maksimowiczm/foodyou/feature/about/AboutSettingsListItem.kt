package com.maksimowiczm.foodyou.feature.about

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    ListItem(
        headlineContent = { Text(stringResource(Res.string.headline_about)) },
        modifier = modifier.clickable { onClick() },
        supportingContent = { Text(stringResource(Res.string.description_about_setting)) },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
            headlineColor = contentColor,
            leadingIconColor = contentColor,
            supportingColor = contentColor,
            trailingIconColor = contentColor
        )
    )
}
