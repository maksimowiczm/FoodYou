package com.maksimowiczm.foodyou.feature.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.about.ui.AboutScreen
import com.maksimowiczm.foodyou.feature.about.ui.AboutSettingsViewModel
import com.maksimowiczm.foodyou.feature.about.ui.buildAboutSettingsListItem
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object AboutSettings : Feature.Settings() {
    override fun build(navController: NavController) = buildAboutSettingsListItem(
        onClick = {
            navController.navigate(
                route = AboutSettings,
                navOptions = navOptions {
                    launchSingleTop = true
                }
            )
        }
    )

    override val module: Module = module {
        viewModelOf(::AboutSettingsViewModel)
    }

    @Serializable
    private data object AboutSettings

    override fun NavGraphBuilder.graph(navController: NavController) {
        forwardBackwardComposable<AboutSettings> {
            AboutScreen()
        }
    }
}
