package com.maksimowiczm.foodyou.ui.personalization

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.maksimowiczm.foodyou.core.ui.SettingsListItem
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun PersonalizationSettingsListItem(
    onClick: () -> Unit,
    shape: Shape,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    SettingsListItem(
        icon = {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = null
            )
        },
        label = {
            Text(stringResource(Res.string.headline_personalization))
        },
        supportingContent = {
            Text(stringResource(Res.string.description_personalization))
        },
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor
    )
}
