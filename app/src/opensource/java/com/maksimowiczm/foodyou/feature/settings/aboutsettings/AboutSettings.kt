package com.maksimowiczm.foodyou.feature.settings.aboutsettings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.data.OpenSourceLinkHandler
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.settings.aboutsettings.ui.AboutScreen
import com.maksimowiczm.foodyou.feature.settings.aboutsettings.ui.AboutSettingsViewModel
import com.maksimowiczm.foodyou.feature.settings.aboutsettings.ui.buildAboutSettingsListItem
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

class AboutSettings(
    private val openSourceLinkHandler: Module.() -> KoinDefinition<OpenSourceLinkHandler>
) : Feature.Settings {
    override fun KoinApplication.module() = module {
        viewModelOf(::AboutSettingsViewModel)

        openSourceLinkHandler()
    }

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
