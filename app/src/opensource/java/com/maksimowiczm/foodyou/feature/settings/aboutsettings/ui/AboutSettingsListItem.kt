package com.maksimowiczm.foodyou.feature.settings.aboutsettings.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

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
                text = stringResource(R.string.headline_about)
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_info_24),
                contentDescription = null
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.description_about_setting)
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
