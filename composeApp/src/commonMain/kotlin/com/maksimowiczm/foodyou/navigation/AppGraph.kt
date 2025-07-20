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
    homeMealCardOnAdd: (epochDay: Long, mealId: Long) -> Unit,
    homeMealCardOnEditMeasurement: (measurementId: Long) -> Unit,
    homeMealCardOnLongClick: () -> Unit,
    settingsOnMeals: () -> Unit,
    settingsOnLanguage: () -> Unit,
    settingsOnSponsor: () -> Unit,
    settingsOnAbout: () -> Unit,
    settingsOnBack: () -> Unit
) {
    forwardBackwardComposable<Home> {
        HomeScreen(
            onSettings = homeOnSettings,
            onAbout = homeOnAbout,
            mealCardOnAdd = homeMealCardOnAdd,
            mealCardOnEditMeasurement = homeMealCardOnEditMeasurement,
            mealCardOnLongClick = { homeMealCardOnLongClick() }
        )
    }
    forwardBackwardComposable<Settings> {
        SettingsScreen(
            onMeals = settingsOnMeals,
            onBack = settingsOnBack,
            onLanguage = settingsOnLanguage,
            onSponsor = settingsOnSponsor,
            onAbout = settingsOnAbout
        )
    }
}
