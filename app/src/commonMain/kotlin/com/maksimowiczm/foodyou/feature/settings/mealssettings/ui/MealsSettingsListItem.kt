package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

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
import com.maksimowiczm.foodyou.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun MealsSettingsListItem(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(stringResource(R.string.headline_meals))
        },
        modifier = modifier
            .clickable { onClick() }
            .horizontalDisplayCutoutPadding(),
        supportingContent = {
            Text(stringResource(R.string.neutral_set_your_meal_schedule))
        },
        leadingContent = {
            Icon(
                painter = painterResource(R.drawable.ic_restaurant_24),
                contentDescription = null
            )
        }
    )
}

@Preview
@Composable
private fun MealsSettingsListItemPreview() {
    FoodYouTheme {
        MealsSettingsListItem(
            onClick = {}
        )
    }
}
