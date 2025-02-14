package com.maksimowiczm.foodyou.core.feature.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.about.data.AndroidLinkHandler
import com.maksimowiczm.foodyou.core.feature.about.data.LinkHandler
import com.maksimowiczm.foodyou.core.feature.about.ui.AboutScreen
import com.maksimowiczm.foodyou.core.feature.about.ui.AboutSettingsViewModel
import com.maksimowiczm.foodyou.core.feature.about.ui.buildAboutSettingsListItem
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val AboutModule = module {
    factoryOf(::AndroidLinkHandler).bind<LinkHandler>()
    viewModelOf(::AboutSettingsViewModel)
}

object AboutFeature : Feature.Koin, Feature.Settings {
    override fun KoinApplication.setup() {
        modules(AboutModule)
    }

    override fun NavGraphBuilder.settingsGraph(navController: NavController) {
        forwardBackwardComposable<AboutSettings> {
            AboutScreen()
        }
    }

    override fun buildSettingsFeatures(navController: NavController) = listOf(
        buildAboutSettingsListItem(
            onClick = {
                navController.navigateToAbout(
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        )
    )

    @Serializable
    data object AboutSettings

    fun NavController.navigateToAbout(navOptions: NavOptions? = null) =
        navigate(AboutSettings, navOptions)
}
