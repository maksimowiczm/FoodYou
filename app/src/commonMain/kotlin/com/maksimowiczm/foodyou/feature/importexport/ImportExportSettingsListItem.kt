package com.maksimowiczm.foodyou.feature.importexport

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ImportExportSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier.clickable { onClick() },
        headlineContent = { Text(stringResource(Res.string.headline_import_and_export)) },
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_database),
                contentDescription = null
            )
        },
        supportingContent = { Text(stringResource(Res.string.description_import_and_export)) }
    )
}
