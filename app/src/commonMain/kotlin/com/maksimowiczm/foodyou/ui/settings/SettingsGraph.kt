package com.maksimowiczm.foodyou.ui.settings

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import kotlinx.serialization.Serializable

@Serializable
data object Settings

fun NavGraphBuilder.settingsGraph(
    onBack: () -> Unit,
    onMealsSettings: () -> Unit,
    onGoalsSettings: () -> Unit,
    onAbout: () -> Unit,
    onLanguage: () -> Unit
) {
    crossfadeComposable<Settings> {
        SettingsScreen(
            onBack = onBack,
            onMealsSettings = onMealsSettings,
            onGoalsSettings = onGoalsSettings,
            onAbout = onAbout,
            onLanguage = onLanguage
        )
    }
}
