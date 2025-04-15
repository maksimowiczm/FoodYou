package com.maksimowiczm.foodyou.ui.settings

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import kotlinx.serialization.Serializable

@Serializable
data object Settings

@Serializable
data object HomeSettings

fun NavGraphBuilder.settingsGraph(
    onSettingsBack: () -> Unit,
    onHomeSettingsBack: () -> Unit,
    onHomeSettings: () -> Unit,
    onOpenFoodFactsSettings: () -> Unit,
    onMealsSettings: () -> Unit,
    onGoalsSettings: () -> Unit,
    onAbout: () -> Unit,
    onLanguage: () -> Unit
) {
    crossfadeComposable<Settings> {
        SettingsScreen(
            onBack = onSettingsBack,
            onHomeSettings = onHomeSettings,
            onOpenFoodFactsSettings = onOpenFoodFactsSettings,
            onGoalsSettings = onGoalsSettings,
            onAbout = onAbout,
            onLanguage = onLanguage
        )
    }
    forwardBackwardComposable<HomeSettings> {
        HomeSettingsScreen(
            onBack = onHomeSettingsBack,
            onMealsSettings = onMealsSettings
        )
    }
}
