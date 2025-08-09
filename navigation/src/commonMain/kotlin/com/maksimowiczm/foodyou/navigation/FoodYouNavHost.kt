package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.navigation.domain.AboutDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorMessagesDestination
import com.maksimowiczm.foodyou.navigation.domain.FoodDiarySearchDestination
import com.maksimowiczm.foodyou.navigation.domain.GoalsCardSettingsDestination
import com.maksimowiczm.foodyou.navigation.domain.GoalsMasterDestination
import com.maksimowiczm.foodyou.navigation.domain.HomeDestination
import com.maksimowiczm.foodyou.navigation.domain.MealsCardsSettingsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsDatabaseDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsExternalDatabasesDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsGoalsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsHomeDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsLanguageDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsMealsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsNutritionFactsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsPersonalizationDestination
import com.maksimowiczm.foodyou.navigation.graph.about.aboutNavigationGraph
import com.maksimowiczm.foodyou.navigation.graph.fooddiary.foodDiaryNavigationGraph
import com.maksimowiczm.foodyou.navigation.graph.goals.goalsNavigationGraph
import com.maksimowiczm.foodyou.navigation.graph.home.homeNavigationGraph
import com.maksimowiczm.foodyou.navigation.graph.settings.settingsNavigationGraph

@Composable
fun FoodYouNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeDestination,
        modifier = modifier,
    ) {
        homeNavigationGraph(
            masterOnSettings = { navController.navigateSingleTop(SettingsDestination) },
            masterOnTitle = { navController.navigateSingleTop(AboutDestination) },
            masterOnMealCardsSettings = {
                navController.navigateSingleTop(MealsCardsSettingsDestination)
            },
            masterOnFoodDiarySearch = { epochDay, mealId ->
                navController.navigateSingleTop(FoodDiarySearchDestination(epochDay, mealId))
            },
            masterOnGoalsCardSettings = {
                navController.navigateSingleTop(GoalsCardSettingsDestination)
            },
            masterOnGoals = { navController.navigateSingleTop(GoalsMasterDestination(it)) },
            mealsCardsSettingsOnBack = {
                navController.popBackStack<MealsCardsSettingsDestination>(true)
            },
            mealsCardsSettingsOnMealSettings = {
                navController.navigateSingleTop(SettingsMealsDestination)
            },
            goalsCardSettingsOnBack = {
                navController.popBackStack<GoalsCardSettingsDestination>(true)
            },
            goalsCardSettingsOnGoalsSettings = {
                navController.navigateSingleTop(GoalsCardSettingsDestination)
            },
        )
        aboutNavigationGraph(
            masterOnBack = { navController.popBackStack<AboutDestination>(true) },
            masterOnSponsor = { navController.navigateSingleTop(AboutSponsorMessagesDestination) },
            sponsorMessagesOnBack = {
                navController.popBackStack<AboutSponsorMessagesDestination>(true)
            },
            sponsorMessagesOnSponsor = { navController.navigateSingleTop(AboutSponsorDestination) },
            sponsorOnBack = { navController.popBackStack<AboutSponsorDestination>(true) },
        )
        settingsNavigationGraph(
            masterOnBack = { navController.popBackStack<SettingsDestination>(true) },
            masterOnSponsor = { navController.navigateSingleTop(AboutSponsorMessagesDestination) },
            masterOnAbout = { navController.navigateSingleTop(AboutDestination) },
            masterOnMeals = { navController.navigateSingleTop(SettingsMealsDestination) },
            masterOnLanguage = { navController.navigateSingleTop(SettingsLanguageDestination) },
            masterOnGoals = { navController.navigateSingleTop(SettingsGoalsDestination) },
            masterOnPersonalization = {
                navController.navigateSingleTop(SettingsPersonalizationDestination)
            },
            masterOnDatabase = { navController.navigateSingleTop(SettingsDatabaseDestination) },
            mealsOnBack = { navController.popBackStack<SettingsMealsDestination>(true) },
            mealsOnMealsCardsSettings = {
                navController.navigateSingleTop(MealsCardsSettingsDestination)
            },
            languageOnBack = { navController.popBackStack<SettingsLanguageDestination>(true) },
            goalsOnBack = { navController.popBackStack<SettingsGoalsDestination>(true) },
            goalsOnSave = { navController.popBackStack<SettingsGoalsDestination>(true) },
            personalizationOnBack = {
                navController.popBackStack<SettingsPersonalizationDestination>(true)
            },
            personalizationOnHome = { navController.navigateSingleTop(SettingsHomeDestination) },
            personalizationOnNutrition = {
                navController.navigateSingleTop(SettingsNutritionFactsDestination)
            },
            nutritionOnBack = {
                navController.popBackStack<SettingsNutritionFactsDestination>(true)
            },
            homeOnBack = { navController.popBackStack<SettingsHomeDestination>(true) },
            homeOnGoals = { navController.navigateSingleTop(GoalsCardSettingsDestination) },
            homeOnMeals = { navController.navigateSingleTop(MealsCardsSettingsDestination) },
            databaseOnBack = { navController.popBackStack<SettingsDatabaseDestination>(true) },
            databaseOnExternalDatabases = {
                navController.navigateSingleTop(SettingsExternalDatabasesDestination)
            },
            externalDatabasesOnBack = {
                navController.popBackStack<SettingsExternalDatabasesDestination>(true)
            },
            externalDatabasesOnSwissFoodCompositionDatabase = {
                // TODO
            },
        )
        goalsNavigationGraph(
            masterOnBack = { navController.popBackStack<GoalsMasterDestination>(true) }
        )
        foodDiaryNavigationGraph(
            searchOnBack = { navController.popBackStack<FoodDiarySearchDestination>(true) },
            searchOnCreateRecipe = {
                // TODO
            },
            searchOnCreateProduct = {
                // TODO
            },
        )
    }
}

private fun <T : Any> NavController.navigateSingleTop(route: T) =
    navigate(route) { launchSingleTop = true }
