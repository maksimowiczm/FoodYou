package com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings

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
import com.maksimowiczm.foodyou.core.feature.product.ProductFeature

fun buildFoodDatabaseSettingsListItem(
    navController: NavController
) = SettingsFeature { modifier ->
    FoodDatabaseSettingsListItem(
        onFoodDatabaseClick = {
            navController.navigate(ProductFeature.FoodDatabaseSettings)
        },
        modifier = modifier
    )
}

@Composable
private fun FoodDatabaseSettingsListItem(
    onFoodDatabaseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.headline_food_database)
            )
        },
        modifier = modifier.clickable(onClick = onFoodDatabaseClick),
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
    FoodDatabaseSettingsListItem(onFoodDatabaseClick = {})
}
