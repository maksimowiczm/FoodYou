package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.feature.about.About
import com.maksimowiczm.foodyou.feature.about.aboutGraph
import com.maksimowiczm.foodyou.feature.addfood.AddFoodMeal
import com.maksimowiczm.foodyou.feature.addfood.AddFoodSearchFood
import com.maksimowiczm.foodyou.feature.addfood.addFoodGraph
import com.maksimowiczm.foodyou.feature.goals.GoalsScreen
import com.maksimowiczm.foodyou.feature.goals.GoalsSettings
import com.maksimowiczm.foodyou.feature.goals.goalsGraph
import com.maksimowiczm.foodyou.feature.language.Language
import com.maksimowiczm.foodyou.feature.language.languageGraph
import com.maksimowiczm.foodyou.feature.meal.MealsSettings
import com.maksimowiczm.foodyou.feature.meal.mealGraph
import com.maksimowiczm.foodyou.feature.productredesign.ui.create.CreateProductApp
import com.maksimowiczm.foodyou.ui.home.Home
import com.maksimowiczm.foodyou.ui.home.homeGraph
import com.maksimowiczm.foodyou.ui.settings.Settings
import com.maksimowiczm.foodyou.ui.settings.settingsGraph

@Composable
fun FoodYouNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "elo"
    ) {
        composable("elo") {
            CreateProductApp(
                onBack = {
                    navController.popBackStack()
                },
                onProductCreate = {
                    navController.navigate(Home) {
                        launchSingleTop = true
                    }
                }
            )
        }

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
            },
            onCaloriesCardClick = { epochDay ->
                navController.navigate(
                    GoalsScreen(epochDay)
                ) {
                    launchSingleTop = true
                }
            }
        )
        settingsGraph(
            onBack = {
                navController.popBackStack<Settings>(inclusive = true)
            },
            onMealsSettings = {
                navController.navigate(MealsSettings) {
                    launchSingleTop = true
                }
            },
            onGoalsSettings = {
                navController.navigate(GoalsSettings) {
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
        addFoodGraph(
            onBack = {
                navController.popBackStack<AddFoodSearchFood>(inclusive = true)
                navController.popBackStack<AddFoodMeal>(inclusive = true)
            }
        )
        goalsGraph(
            onGoalsSettingsBack = {
                navController.popBackStack<GoalsSettings>(inclusive = true)
            }
        )
        languageGraph(
            onBack = {
                navController.popBackStack<Language>(inclusive = true)
            }
        )
        mealGraph(
            onMealsSettingsBack = {
                navController.popBackStack<MealsSettings>(inclusive = true)
            }
        )
    }
}
