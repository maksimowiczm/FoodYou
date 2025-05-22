package com.maksimowiczm.foodyou.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
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
fun HomeSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    SettingsListItem(
        headlineContent = { Text(stringResource(Res.string.headline_home)) },
        onClick = onClick,
        modifier = modifier,
        supportingContent = { Text(stringResource(Res.string.action_edit_your_home_page)) },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = null
            )
        },
        containerColor = containerColor,
        contentColor = contentColor
    )
}
