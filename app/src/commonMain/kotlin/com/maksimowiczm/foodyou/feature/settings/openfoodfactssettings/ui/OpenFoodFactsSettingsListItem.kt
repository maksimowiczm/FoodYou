package com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
                text = stringResource(Res.string.headline_remote_food_database)
            )
        },
        modifier = modifier
            .clickable(onClick = onClick)
            .horizontalDisplayCutoutPadding(),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_cloud_download),
                contentDescription = null
            )
        },
        supportingContent = {
            Text(
                text = stringResource(Res.string.neutral_manage_food_database)
            )
        }
    )
}

@Preview
@Composable
private fun FoodDatabaseSettingsListItemPreview() {
    OpenFoodFactsSettingsListItem(onClick = {})
}
