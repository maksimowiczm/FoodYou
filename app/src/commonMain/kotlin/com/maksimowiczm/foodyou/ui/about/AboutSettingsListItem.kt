package com.maksimowiczm.foodyou.ui.about

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.core.ui.component.SettingsListItem
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    SettingsListItem(
        headlineContent = { Text(stringResource(Res.string.headline_about)) },
        onClick = onClick,
        modifier = modifier,
        supportingContent = { Text(stringResource(Res.string.description_about_setting)) },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null
            )
        },
        containerColor = containerColor,
        contentColor = contentColor
    )
}
