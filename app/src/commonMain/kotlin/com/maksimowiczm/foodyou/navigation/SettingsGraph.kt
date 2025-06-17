package com.maksimowiczm.foodyou.navigation

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.ui.about.AboutScreen
import com.maksimowiczm.foodyou.ui.externaldatabases.ExternalDatabasesScreen
import com.maksimowiczm.foodyou.ui.home.HomeSettingsScreen
import com.maksimowiczm.foodyou.ui.personalize.PersonalizeNutritionFactsScreen
import com.maksimowiczm.foodyou.ui.personalize.PersonalizeSettingsScreen
import com.maksimowiczm.foodyou.ui.settings.SettingsScreen
import com.maksimowiczm.foodyou.ui.sponsor.SponsorScreen
import kotlinx.serialization.Serializable

@Serializable
data object Settings

@Serializable
data object HomeSettings

@Serializable
data object ExternalDatabases

@Serializable
data object About

@Serializable
data object Sponsor

@Serializable
data object PersonalizeSettings

@Serializable
data object PersonalizeNutritionFactsSettings

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
    onSwissFoodCompositionDatabase: () -> Unit,
    aboutOnBack: () -> Unit,
    aboutOnSponsor: () -> Unit,
    sponsorOnBack: () -> Unit,
    personalizeOnBack: () -> Unit,
    onPersonalizeSettings: () -> Unit,
    onPersonalizeNutritionFacts: () -> Unit,
    personalizeNutritionFactsOnBack: () -> Unit
) {
    forwardBackwardComposable<Settings> {
        SettingsScreen(
            onBack = settingsOnBack,
            onMealsSettings = onMealsSettings,
            onGoalsSettings = onGoalsSettings,
            onAbout = onAbout,
            onLanguage = onLanguage,
            onImportExport = onImportExport,
            onExternalDatabases = onExternalDatabases,
            onPersonalizeSettings = onPersonalizeSettings
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
    forwardBackwardComposable<About> {
        AboutScreen(
            onBack = aboutOnBack,
            onSponsor = aboutOnSponsor
        )
    }
    forwardBackwardComposable<Sponsor> {
        SponsorScreen(
            onBack = sponsorOnBack
        )
    }
    forwardBackwardComposable<PersonalizeSettings> {
        PersonalizeSettingsScreen(
            onBack = personalizeOnBack,
            onHomeSettings = onHomeSettings,
            onNutritionFacts = onPersonalizeNutritionFacts
        )
    }
    forwardBackwardComposable<PersonalizeNutritionFactsSettings> {
        PersonalizeNutritionFactsScreen(
            onBack = personalizeNutritionFactsOnBack
        )
    }
}
