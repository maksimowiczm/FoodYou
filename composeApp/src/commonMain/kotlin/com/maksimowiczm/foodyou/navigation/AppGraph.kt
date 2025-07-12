package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.ui.home.HomeScreen
import com.maksimowiczm.foodyou.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data object Settings

fun NavGraphBuilder.appGraph(
    homeOnAbout: () -> Unit,
    homeOnSettings: () -> Unit,
    settingsOnBack: () -> Unit
) {
    forwardBackwardComposable<Home> {
        HomeScreen(
            onSettings = homeOnSettings,
            onAbout = homeOnAbout
        )
    }
    forwardBackwardComposable<Settings> {
        SettingsScreen(
            onBack = settingsOnBack
        )
    }
}
