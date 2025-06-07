package com.maksimowiczm.foodyou.ui.settings.externaldatabases

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.core.ui.component.SettingsListItem
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExternalDatabasesSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    SettingsListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_external_databases))
        },
        onClick = onClick,
        supportingContent = {
            Text(stringResource(Res.string.description_external_databases))
        },
        modifier = modifier,
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.CloudDownload,
                contentDescription = null
            )
        },
        containerColor = containerColor,
        contentColor = contentColor
    )
}
