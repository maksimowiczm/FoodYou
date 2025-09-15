package com.maksimowiczm.foodyou.app.ui.settings.opensource

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.maksimowiczm.foodyou.app.ui.shared.component.SettingsListItem
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.description_manage_database
import foodyou.app.generated.resources.headline_database
import foodyou.app.generated.resources.ic_database
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DatabaseSettingsListItem(
    onClick: () -> Unit,
    shape: Shape,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    SettingsListItem(
        icon = { Icon(painterResource(Res.drawable.ic_database), null) },
        label = { Text(stringResource(Res.string.headline_database)) },
        supportingContent = { Text(stringResource(Res.string.description_manage_database)) },
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
    )
}
