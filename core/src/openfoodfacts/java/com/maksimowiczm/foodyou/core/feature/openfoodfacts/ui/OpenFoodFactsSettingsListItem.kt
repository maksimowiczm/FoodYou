package com.maksimowiczm.foodyou.core.feature.openfoodfacts.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.SettingsFeature
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.OpenFoodFactsFeature
import com.maksimowiczm.foodyou.core.ui.modifier.horizontalDisplayCutoutPadding

fun buildOpenFoodFactsSettingsListItem(navController: NavController) = SettingsFeature { modifier ->
    OpenFoodFactsSettingsListItem(
        onClick = {
            navController.navigate(OpenFoodFactsFeature.FoodDatabaseSettings)
        },
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
