package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.addfood.navigation.AddFoodFeature
import com.maksimowiczm.foodyou.feature.addfood.navigation.addFoodGraph
import com.maksimowiczm.foodyou.feature.addfood.navigation.navigateToAddFood
import com.maksimowiczm.foodyou.feature.diary.navigation.DiaryFeature
import com.maksimowiczm.foodyou.feature.diary.navigation.diaryGraph
import com.maksimowiczm.foodyou.feature.settings.navigation.SettingsFeature
import com.maksimowiczm.foodyou.feature.settings.navigation.SettingsRoute
import com.maksimowiczm.foodyou.feature.settings.navigation.navigateToSettings
import com.maksimowiczm.foodyou.feature.settings.navigation.settingsGraph

@Composable
fun FoodYouNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = DiaryFeature
    ) {
        diaryGraph(
            onAddProductToMeal = { meal, date ->
                navController.navigateToAddFood(
                    route = AddFoodFeature(
                        meal = meal,
                        epochDay = date.toEpochDay()
                    ),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            onSettings = navController::navigateToSettings
        )
        addFoodGraph(
            onClose = {
                navController.popBackStack()
            },
            onSearchSettings = {
                navController.navigateToSettings(
                    route = SettingsRoute.FoodDatabaseSettings,
                    navOptions = navOptions { launchSingleTop = true }
                )
            }
        )
        settingsGraph(
            onBack = {
                navController.popBackStack(
                    route = SettingsFeature,
                    inclusive = true
                )
            },
            onFoodDatabaseSettings = {
                navController.navigate(
                    route = SettingsRoute.FoodDatabaseSettings,
                    navOptions = navOptions { launchSingleTop = true }
                )
            },
            onFoodDatabaseBack = {
                navController.popBackStack(
                    route = SettingsRoute.FoodDatabaseSettings,
                    inclusive = true
                )
            },
            onGoalsSettings = {
                navController.navigate(
                    route = SettingsRoute.GoalsSettings,
                    navOptions = navOptions { launchSingleTop = true }
                )
            },
            onGoalsBack = {
                navController.popBackStack(
                    route = SettingsRoute.GoalsSettings,
                    inclusive = true
                )
            }
        )
    }
}
