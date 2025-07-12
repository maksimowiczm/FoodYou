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
    settingsOnBack: () -> Unit
) {
    forwardBackwardComposable<Home> {
        HomeScreen(
            onSettings = homeOnSettings,
            onAbout = homeOnAbout,
            mealCardOnAdd = homeMealCardOnAdd,
            mealCardOnEditMeasurement = homeMealCardOnEditMeasurement
        )
    }
    forwardBackwardComposable<Settings> {
        SettingsScreen(
            onBack = settingsOnBack
        )
    }
}
