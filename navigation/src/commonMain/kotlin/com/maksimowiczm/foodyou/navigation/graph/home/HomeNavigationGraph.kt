package com.maksimowiczm.foodyou.navigation.graph.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.maksimowiczm.foodyou.feature.home.ui.HomeScreen
import com.maksimowiczm.foodyou.feature.home.ui.goals.GoalsCardSettings
import com.maksimowiczm.foodyou.feature.home.ui.meals.settings.MealsCardsSettingsScreen
import com.maksimowiczm.foodyou.navigation.domain.GoalsCardSettingsDestination
import com.maksimowiczm.foodyou.navigation.domain.HomeDestination
import com.maksimowiczm.foodyou.navigation.domain.HomeMasterDestination
import com.maksimowiczm.foodyou.navigation.domain.MealsCardsSettingsDestination
import com.maksimowiczm.foodyou.shared.navigation.forwardBackwardComposable

fun NavGraphBuilder.homeNavigationGraph(
    masterOnSettings: () -> Unit,
    masterOnTitle: () -> Unit,
    masterOnMealCardsSettings: () -> Unit,
    masterOnFoodDiarySearch: (epochDay: Long, mealId: Long) -> Unit,
    masterOnGoalsCardSettings: () -> Unit,
    masterOnGoals: (epochDay: Long) -> Unit,
    masterOnEditDiaryEntry: (id: Long) -> Unit,
    mealsCardsSettingsOnBack: () -> Unit,
    mealsCardsSettingsOnMealSettings: () -> Unit,
    goalsCardSettingsOnBack: () -> Unit,
    goalsCardSettingsOnGoalsSettings: () -> Unit,
) {
    navigation<HomeDestination>(startDestination = HomeMasterDestination) {
        forwardBackwardComposable<HomeMasterDestination> {
            HomeScreen(
                onSettings = masterOnSettings,
                onTitle = masterOnTitle,
                onMealCardLongClick = { masterOnMealCardsSettings() },
                onMealCardClick = masterOnFoodDiarySearch,
                onGoalsCardLongClick = masterOnGoalsCardSettings,
                onGoalsCardClick = masterOnGoals,
                onEditMeasurementClick = masterOnEditDiaryEntry,
            )
        }
        forwardBackwardComposable<MealsCardsSettingsDestination> {
            MealsCardsSettingsScreen(
                onBack = mealsCardsSettingsOnBack,
                onMealSettings = mealsCardsSettingsOnMealSettings,
            )
        }
        forwardBackwardComposable<GoalsCardSettingsDestination> {
            GoalsCardSettings(
                onBack = goalsCardSettingsOnBack,
                onGoalsSettings = goalsCardSettingsOnGoalsSettings,
            )
        }
    }
}
