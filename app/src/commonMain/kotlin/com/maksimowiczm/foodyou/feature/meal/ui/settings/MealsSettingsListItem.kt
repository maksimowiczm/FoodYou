package com.maksimowiczm.foodyou.feature.meal.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MealsSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_meals))
        },
        modifier = modifier.clickable { onClick() },
        supportingContent = {
            Text(stringResource(Res.string.neutral_set_your_meal_schedule))
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null
            )
        }
    )
}
