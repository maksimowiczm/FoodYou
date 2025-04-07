package com.maksimowiczm.foodyou.feature.diary.openfoodfacts.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun OpenFoodFactsSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(Res.string.headline_remote_food_database)
            )
        },
        modifier = modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.CloudDownload,
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
