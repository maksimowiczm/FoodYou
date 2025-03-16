package com.maksimowiczm.foodyou.feature.settings.language

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.settings.SettingsFeature
import com.maksimowiczm.foodyou.feature.settings.language.ui.LanguageScreen
import com.maksimowiczm.foodyou.feature.settings.language.ui.LanguageSettingsListItem
import com.maksimowiczm.foodyou.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

class LanguageSettings(
    private val languageSettingsTrailingContent: @Composable (Modifier) -> Unit
) : Feature.Settings {
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
