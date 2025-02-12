package com.maksimowiczm.foodyou.core.feature.diary.ui.mealssettings

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
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.SettingsFeature
import com.maksimowiczm.foodyou.core.feature.diary.DiaryFeature.navigateToMealsSettings
import com.maksimowiczm.foodyou.core.ui.modifier.horizontalDisplayCutoutPadding
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme

fun buildMealsSettingsListItem(navController: NavController) = SettingsFeature { modifier ->
    MealsSettingsListItem(
        onClick = {
            navController.navigateToMealsSettings(
                navOptions = navOptions {
                    launchSingleTop = true
                }
            )
        },
        modifier = modifier
    )
}

@Composable
private fun MealsSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
