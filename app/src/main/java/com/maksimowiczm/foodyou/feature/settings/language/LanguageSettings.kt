package com.maksimowiczm.foodyou.feature.settings.language

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.data.AndroidSystemInfoRepository
import com.maksimowiczm.foodyou.data.LinkHandler
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature
import com.maksimowiczm.foodyou.feature.settings.language.ui.AndroidLanguageViewModel
import com.maksimowiczm.foodyou.feature.settings.language.ui.LanguageScreen
import com.maksimowiczm.foodyou.feature.settings.language.ui.LanguageSettingsListItem
import com.maksimowiczm.foodyou.feature.settings.language.ui.LanguageViewModel
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

class LanguageSettings(
    private val languageSettingsTrailingContent: @Composable (Modifier) -> Unit,
    private val androidSystemInfoRepository:
    Module.() -> KoinDefinition<AndroidSystemInfoRepository>,
    private val linkHandler: Module.() -> KoinDefinition<LinkHandler>
) : Feature.Settings {
    override fun KoinApplication.module() = module {
        viewModelOf(::AndroidLanguageViewModel).bind<LanguageViewModel>()

        androidSystemInfoRepository()
        linkHandler()
    }

    override fun buildSettingsFeatures(navController: NavController) = SettingsFeature { modifier ->
        LanguageSettingsListItem(
            onClick = {
                navController.navigate(
                    route = LanguageRoute,
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            modifier = modifier
        )
    }

    @Serializable
    private data object LanguageRoute

    override fun NavGraphBuilder.graph(navController: NavController) {
        forwardBackwardComposable<LanguageRoute> {
            LanguageScreen(
                onBack = { navController.popBackStack<LanguageRoute>(inclusive = true) },
                trailingContent = languageSettingsTrailingContent
            )
        }
    }
}
