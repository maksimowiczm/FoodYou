package com.maksimowiczm.foodyou.feature.settings.aboutsettings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.settings.aboutsettings.ui.AboutScreen
import com.maksimowiczm.foodyou.feature.settings.aboutsettings.ui.buildAboutSettingsListItem
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

object AboutSettings : Feature.Settings {
    override fun buildSettingsFeatures(navController: NavController) = buildAboutSettingsListItem(
        onClick = {
            navController.navigate(
                route = AboutSettings,
                navOptions = navOptions {
                    launchSingleTop = true
                }
            )
        }
    )

    @Serializable
    private data object AboutSettings

    override fun NavGraphBuilder.graph(navController: NavController) {
        forwardBackwardComposable<AboutSettings> {
            AboutScreen()
        }
    }
}
