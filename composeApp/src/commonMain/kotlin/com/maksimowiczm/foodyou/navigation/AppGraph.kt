package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.food.ui.ExternalDatabasesScreen
import com.maksimowiczm.foodyou.ui.home.HomeScreen
import com.maksimowiczm.foodyou.ui.personalization.HomePersonalizationScreen
import com.maksimowiczm.foodyou.ui.personalization.PersonalizationScreen
import com.maksimowiczm.foodyou.ui.personalization.PersonalizeNutritionFactsScreen
import com.maksimowiczm.foodyou.ui.settings.DatabaseScreen
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

@Serializable
data object ExternalDatabases

@Serializable
data object Database

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
    settingsOnDatabase: () -> Unit,
    settingsOnBack: () -> Unit,
    personalizationOnBack: () -> Unit,
    personalizationOnHomePersonalization: () -> Unit,
    personalizationOnNutritionFactsPersonalization: () -> Unit,
    homePersonalizationOnBack: () -> Unit,
    homePersonalizationOnMealsSettings: () -> Unit,
    nutritionFactsPersonalizationOnBack: () -> Unit,
    externalDatabasesOnBack: () -> Unit,
    externalDatabasesOnSwissFoodCompositionDatabase: () -> Unit,
    databaseOnBack: () -> Unit,
    databaseOnExternalDatabases: () -> Unit
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
            onBack = settingsOnBack,
            onPersonalization = settingsOnPersonalization,
            onMeals = settingsOnMeals,
            onLanguage = settingsOnLanguage,
            onSponsor = settingsOnSponsor,
            onAbout = settingsOnAbout,
            onDatabase = settingsOnDatabase
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
    forwardBackwardComposable<ExternalDatabases> {
        ExternalDatabasesScreen(
            onBack = externalDatabasesOnBack,
            onSwissFoodCompositionDatabase = externalDatabasesOnSwissFoodCompositionDatabase
        )
    }
    forwardBackwardComposable<Database> {
        DatabaseScreen(
            onBack = databaseOnBack,
            onExternalDatabases = databaseOnExternalDatabases
        )
    }
}
