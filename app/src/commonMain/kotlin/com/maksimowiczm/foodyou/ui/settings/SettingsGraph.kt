package com.maksimowiczm.foodyou.ui.settings

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.ui.home.HomeSettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object Settings

@Serializable
data object HomeSettings

fun NavGraphBuilder.settingsGraph(
    onBack: () -> Unit,
    onHomeSettings: () -> Unit,
    onMealsSettings: () -> Unit,
    onMealsCardSettings: () -> Unit,
    onGoalsSettings: () -> Unit,
    onAbout: () -> Unit,
    onLanguage: () -> Unit,
    onImportExport: () -> Unit
) {
    crossfadeComposable<Settings> {
        SettingsScreen(
            onBack = onBack,
            onHomeSettings = onHomeSettings,
            onMealsSettings = onMealsSettings,
            onGoalsSettings = onGoalsSettings,
            onAbout = onAbout,
            onLanguage = onLanguage,
            onImportExport = onImportExport
        )
    }
    forwardBackwardComposable<HomeSettings> {
        HomeSettingsScreen(
            onBack = onBack,
            onMealsSettings = onMealsCardSettings
        )
    }
}
