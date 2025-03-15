package com.maksimowiczm.foodyou.feature.settings.aboutsettings.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

fun buildAboutSettingsListItem(onClick: () -> Unit) = SettingsFeature { modifier ->
    AboutSettingsListItem(
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
private fun AboutSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier
            .clickable { onClick() }
            .horizontalDisplayCutoutPadding(),
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

@Preview
@Composable
private fun AboutSettingsListItemPreview() {
    FoodYouTheme {
        AboutSettingsListItem(
            onClick = {}
        )
    }
}
