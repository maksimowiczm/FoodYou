package com.maksimowiczm.foodyou.feature.about

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VolunteerActivism
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
fun SponsorSettingsListItem(
    onClick: () -> Unit,
    shape: Shape,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    SettingsListItem(
        icon = {
            Icon(
                imageVector = Icons.Outlined.VolunteerActivism,
                contentDescription = null
            )
        },
        label = {
            Text(stringResource(Res.string.headline_sponsor))
        },
        supportingContent = {
            Text(stringResource(Res.string.description_sponsor_short_2))
        },
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor
    )
}
