package com.maksimowiczm.foodyou.ui.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Settings

fun NavGraphBuilder.settingsGraph(onBack: () -> Unit, onAbout: () -> Unit, onLanguage: () -> Unit) {
    composable<Settings> {
        SettingsScreen(
            onBack = onBack,
            onAbout = onAbout,
            onLanguage = onLanguage
        )
    }
}
