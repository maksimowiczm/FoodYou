package com.maksimowiczm.foodyou.navigation.graph.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.maksimowiczm.foodyou.feature.settings.goals.ui.DailyGoalsScreen
import com.maksimowiczm.foodyou.feature.settings.language.ui.LanguageScreen
import com.maksimowiczm.foodyou.feature.settings.master.ui.SettingsScreen
import com.maksimowiczm.foodyou.feature.settings.meal.ui.MealSettingsScreen
import com.maksimowiczm.foodyou.feature.settings.personalization.ui.HomePersonalizationScreen
import com.maksimowiczm.foodyou.feature.settings.personalization.ui.PersonalizationScreen
import com.maksimowiczm.foodyou.feature.settings.personalization.ui.PersonalizeNutritionFactsScreen
import com.maksimowiczm.foodyou.navigation.domain.SettingsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsGoalsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsHomeDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsLanguageDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsMasterDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsMealsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsNutritionFactsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsPersonalizationDestination
import com.maksimowiczm.foodyou.shared.navigation.forwardBackwardComposable

internal fun NavGraphBuilder.settingsNavigationGraph(
    masterOnBack: () -> Unit,
    masterOnSponsor: () -> Unit,
    masterOnAbout: () -> Unit,
    masterOnMeals: () -> Unit,
    masterOnLanguage: () -> Unit,
    masterOnGoals: () -> Unit,
    masterOnPersonalization: () -> Unit,
    mealsOnBack: () -> Unit,
    mealsOnMealsCardsSettings: () -> Unit,
    goalsOnBack: () -> Unit,
    goalsOnSave: () -> Unit,
    languageOnBack: () -> Unit,
    personalizationOnBack: () -> Unit,
    personalizationOnHome: () -> Unit,
    personalizationOnNutrition: () -> Unit,
    nutritionOnBack: () -> Unit,
    homeOnBack: () -> Unit,
    homeOnGoals: () -> Unit,
    homeOnMeals: () -> Unit,
) {
    navigation<SettingsDestination>(startDestination = SettingsMasterDestination) {
        forwardBackwardComposable<SettingsMasterDestination> {
            SettingsScreen(
                onBack = masterOnBack,
                onSponsor = masterOnSponsor,
                onAbout = masterOnAbout,
                onMeals = masterOnMeals,
                onGoals = masterOnGoals,
                onPersonalization = masterOnPersonalization,
                onLanguage = masterOnLanguage,
            )
        }
        forwardBackwardComposable<SettingsMealsDestination> {
            MealSettingsScreen(
                onBack = mealsOnBack,
                onMealsCardsSettings = mealsOnMealsCardsSettings,
            )
        }
        forwardBackwardComposable<SettingsLanguageDestination> {
            LanguageScreen(onBack = languageOnBack)
        }
        forwardBackwardComposable<SettingsGoalsDestination> {
            DailyGoalsScreen(onBack = goalsOnBack, onSave = goalsOnSave)
        }
        forwardBackwardComposable<SettingsPersonalizationDestination> {
            PersonalizationScreen(
                onBack = personalizationOnBack,
                onHomePersonalization = personalizationOnHome,
                onNutritionFactsPersonalization = personalizationOnNutrition,
            )
        }
        forwardBackwardComposable<SettingsNutritionFactsDestination> {
            PersonalizeNutritionFactsScreen(onBack = nutritionOnBack)
        }
        forwardBackwardComposable<SettingsHomeDestination> {
            HomePersonalizationScreen(
                onBack = homeOnBack,
                onGoals = homeOnGoals,
                onMeals = homeOnMeals,
            )
        }
    }
}
