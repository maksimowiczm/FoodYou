package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.feature.about.About
import com.maksimowiczm.foodyou.feature.about.aboutGraph
import com.maksimowiczm.foodyou.feature.diary.addfood.AddFoodMeal
import com.maksimowiczm.foodyou.feature.diary.addfood.AddFoodSearchFood
import com.maksimowiczm.foodyou.feature.diary.diaryGraph
import com.maksimowiczm.foodyou.feature.diary.mealssettings.MealsSettings
import com.maksimowiczm.foodyou.feature.diary.openfoodfacts.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.feature.language.Language
import com.maksimowiczm.foodyou.feature.language.languageGraph
import com.maksimowiczm.foodyou.ui.home.Home
import com.maksimowiczm.foodyou.ui.home.homeGraph
import com.maksimowiczm.foodyou.ui.settings.Settings
import com.maksimowiczm.foodyou.ui.settings.settingsGraph

@Composable
fun FoodYouNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        homeGraph(
            onSettings = {
                navController.navigate(Settings) {
                    launchSingleTop = true
                }
            },
            onMealCardClick = { epochDay, mealId ->
                navController.navigate(
                    AddFoodMeal(
                        mealId = mealId,
                        epochDay = epochDay
                    )
                ) {
                    launchSingleTop = true
                }
            },
            onMealCardAddClick = { epochDay, mealId ->
                navController.navigate(
                    AddFoodSearchFood(
                        mealId = mealId,
                        epochDay = epochDay
                    )
                ) {
                    launchSingleTop = true
                }
            }
        )
        settingsGraph(
            onBack = {
                navController.popBackStack<Settings>(inclusive = true)
            },
            onOpenFoodFactsSettings = {
                navController.navigate(OpenFoodFactsSettings) {
                    launchSingleTop = true
                }
            },
            onMealsSettings = {
                navController.navigate(MealsSettings) {
                    launchSingleTop = true
                }
            },
            onAbout = {
                navController.navigate(About) {
                    launchSingleTop = true
                }
            },
            onLanguage = {
                navController.navigate(Language) {
                    launchSingleTop = true
                }
            }
        )
        aboutGraph()
        languageGraph(
            onBack = {
                navController.popBackStack<Language>(inclusive = true)
            }
        )
        diaryGraph(
            onMealsSettingsBack = {
                navController.popBackStack<MealsSettings>(inclusive = true)
            },
            onOpenFoodFactsSettings = {
                navController.navigate(OpenFoodFactsSettings) {
                    launchSingleTop = true
                }
            },
            onOpenFoodFactsSettingsBack = {
                navController.popBackStack<OpenFoodFactsSettings>(inclusive = true)
            }
        )
    }
}
