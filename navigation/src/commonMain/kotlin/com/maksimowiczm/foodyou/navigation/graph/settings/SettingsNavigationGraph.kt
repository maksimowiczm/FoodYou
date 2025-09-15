package com.maksimowiczm.foodyou.navigation.graph.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.maksimowiczm.foodyou.app.ui.language.LanguageScreen
import com.maksimowiczm.foodyou.app.ui.theme.ThemeScreen
import com.maksimowiczm.foodyou.feature.food.diary.meal.MealSettingsScreen
import com.maksimowiczm.foodyou.feature.goals.setup.DailyGoalsScreen
import com.maksimowiczm.foodyou.feature.home.personalization.HomePersonalizationScreen
import com.maksimowiczm.foodyou.feature.settings.master.ui.SettingsScreen
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
import com.maksimowiczm.foodyou.navigation.domain.ThemeDestination
import com.maksimowiczm.foodyou.shared.compose.navigation.forwardBackwardComposable

internal fun NavGraphBuilder.settingsNavigationGraph(
    masterOnBack: () -> Unit,
    masterOnSponsor: () -> Unit,
    masterOnAbout: () -> Unit,
    masterOnMeals: () -> Unit,
    masterOnLanguage: () -> Unit,
    masterOnGoals: () -> Unit,
    masterOnPersonalization: () -> Unit,
    masterOnDatabase: () -> Unit,
    mealsOnBack: () -> Unit,
    mealsOnMealsCardsSettings: () -> Unit,
    goalsOnBack: () -> Unit,
    goalsOnSave: () -> Unit,
    languageOnBack: () -> Unit,
    personalizationOnBack: () -> Unit,
    personalizationOnHome: () -> Unit,
    personalizationOnNutrition: () -> Unit,
    personalizationOnTheme: () -> Unit,
    nutritionOnBack: () -> Unit,
    homeOnBack: () -> Unit,
    homeOnGoals: () -> Unit,
    homeOnMeals: () -> Unit,
    themeOnBack: () -> Unit,
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
                onDatabase = masterOnDatabase,
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
                onTheme = personalizationOnTheme,
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
        forwardBackwardComposable<ThemeDestination> { ThemeScreen(onBack = themeOnBack) }
    }
}
