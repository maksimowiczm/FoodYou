package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.ui.home.HomeScreen
import com.maksimowiczm.foodyou.ui.personalization.HomePersonalizationScreen
import com.maksimowiczm.foodyou.ui.personalization.PersonalizationScreen
import com.maksimowiczm.foodyou.ui.personalization.PersonalizeNutritionFactsScreen
import com.maksimowiczm.foodyou.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data object Settings

@Serializable
data object Personalization

@Serializable
data object HomePersonalization

@Serializable
data object NutritionFactsPersonalization

fun NavGraphBuilder.appGraph(
    homeOnAbout: () -> Unit,
    homeOnSettings: () -> Unit,
    homeMealCardOnAdd: (epochDay: Long, mealId: Long) -> Unit,
    homeMealCardOnEditMeasurement: (measurementId: Long) -> Unit,
    homeMealCardOnLongClick: () -> Unit,
    settingsOnPersonalization: () -> Unit,
    settingsOnMeals: () -> Unit,
    settingsOnLanguage: () -> Unit,
    settingsOnSponsor: () -> Unit,
    settingsOnAbout: () -> Unit,
    settingsOnBack: () -> Unit,
    personalizationOnBack: () -> Unit,
    personalizationOnHomePersonalization: () -> Unit,
    personalizationOnNutritionFactsPersonalization: () -> Unit,
    homePersonalizationOnBack: () -> Unit,
    homePersonalizationOnMealsSettings: () -> Unit,
    nutritionFactsPersonalizationOnBack: () -> Unit
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
            onPersonalization = settingsOnPersonalization,
            onMeals = settingsOnMeals,
            onBack = settingsOnBack,
            onLanguage = settingsOnLanguage,
            onSponsor = settingsOnSponsor,
            onAbout = settingsOnAbout
        )
    }
    forwardBackwardComposable<Personalization> {
        PersonalizationScreen(
            onBack = personalizationOnBack,
            onHomePersonalization = personalizationOnHomePersonalization,
            onNutritionFactsPersonalization = personalizationOnNutritionFactsPersonalization
        )
    }
    forwardBackwardComposable<HomePersonalization> {
        HomePersonalizationScreen(
            onBack = homePersonalizationOnBack,
            onMealsSettings = homePersonalizationOnMealsSettings
        )
    }
    forwardBackwardComposable<NutritionFactsPersonalization> {
        PersonalizeNutritionFactsScreen(
            onBack = nutritionFactsPersonalizationOnBack
        )
    }
}
