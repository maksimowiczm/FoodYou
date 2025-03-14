package com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.ui

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

fun buildOpenFoodFactsSettingsListItem(onClick: () -> Unit) = SettingsFeature { modifier ->
    OpenFoodFactsSettingsListItem(
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun OpenFoodFactsSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.headline_remote_food_database)
            )
        },
        modifier = modifier
            .clickable(onClick = onClick)
            .horizontalDisplayCutoutPadding(),
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_cloud_download_24),
                contentDescription = null
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.neutral_manage_food_database)
            )
        }
    )
}

@Preview
@Composable
private fun FoodDatabaseSettingsListItemPreview() {
    OpenFoodFactsSettingsListItem(onClick = {})
}
