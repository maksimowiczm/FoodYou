package com.maksimowiczm.foodyou.feature.settings.mealssettings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealsSettingsListItem
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealsSettingsScreen
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

object MealsSettings : Feature.Settings {
    override fun buildSettingsFeatures(navController: NavController) = SettingsFeature { modifier ->
        MealsSettingsListItem(
            onClick = {
                navController.navigate(
                    route = MealsSettings,
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            modifier = modifier
        )
    }

    @Serializable
    private data object MealsSettings

    override fun NavGraphBuilder.graph(navController: NavController) {
        forwardBackwardComposable<MealsSettings> {
            MealsSettingsScreen(
                onBack = {
                    navController.popBackStack<MealsSettings>(
                        inclusive = true
                    )
                }
            )
        }
    }
}
