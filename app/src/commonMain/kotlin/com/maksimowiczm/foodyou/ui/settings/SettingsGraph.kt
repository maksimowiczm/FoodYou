package com.maksimowiczm.foodyou.ui.settings

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.ui.home.HomeSettingsScreen
import com.maksimowiczm.foodyou.ui.settings.externaldatabases.ExternalDatabasesScreen
import kotlinx.serialization.Serializable

@Serializable
data object Settings

@Serializable
data object HomeSettings

@Serializable
data object ExternalDatabases

fun NavGraphBuilder.settingsGraph(
    settingsOnBack: () -> Unit,
    homeSettingsOnBack: () -> Unit,
    onHomeSettings: () -> Unit,
    onMealsSettings: () -> Unit,
    onMealsCardSettings: () -> Unit,
    onGoalsSettings: () -> Unit,
    onGoalsCardSettings: () -> Unit,
    onAbout: () -> Unit,
    onLanguage: () -> Unit,
    onImportExport: () -> Unit,
    onExternalDatabases: () -> Unit,
    externalDatabasesOnBack: () -> Unit,
    onSwissFoodCompositionDatabase: () -> Unit
) {
    forwardBackwardComposable<Settings> {
        SettingsScreen(
            onBack = settingsOnBack,
            onHomeSettings = onHomeSettings,
            onMealsSettings = onMealsSettings,
            onGoalsSettings = onGoalsSettings,
            onAbout = onAbout,
            onLanguage = onLanguage,
            onImportExport = onImportExport,
            onExternalDatabases = onExternalDatabases
        )
    }
    forwardBackwardComposable<HomeSettings> {
        HomeSettingsScreen(
            onBack = homeSettingsOnBack,
            onMealsSettings = onMealsCardSettings,
            onGoalsSettings = onGoalsCardSettings
        )
    }
    forwardBackwardComposable<ExternalDatabases> {
        ExternalDatabasesScreen(
            onBack = externalDatabasesOnBack,
            onSwissFoodCompositionDatabase = onSwissFoodCompositionDatabase
        )
    }
}
