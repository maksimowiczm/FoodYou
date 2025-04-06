package com.maksimowiczm.foodyou.ui.settings

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

@Serializable
data object Settings

fun NavGraphBuilder.settingsGraph(onBack: () -> Unit, onAbout: () -> Unit, onLanguage: () -> Unit) {
    forwardBackwardComposable<Settings> {
        SettingsScreen(
            onBack = onBack,
            onAbout = onAbout,
            onLanguage = onLanguage
        )
    }
}
