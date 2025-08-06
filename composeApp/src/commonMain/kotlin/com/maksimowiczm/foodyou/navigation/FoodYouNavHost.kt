package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun FoodYouNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {
        appGraph(
            homeOnAbout = {

            },
            homeOnSettings = {
                navController.navigate(Settings) {
                    launchSingleTop = true
                }
            },
            homeGoalsCardOnClick = {

            },
            homeGoalsCardOnLongClick = {

            },
            homeMealCardOnAdd = { epochDay, mealId ->

            },
            homeMealCardOnEditMeasurement = {

            },
            homeMealCardOnLongClick = {

            },
            settingsOnPersonalization = {
                navController.navigate(Personalization) {
                    launchSingleTop = true
                }
            },
            settingsOnMeals = {

            },
            settingsOnLanguage = {

            },
            settingsOnSponsor = {

            },
            settingsOnAbout = {

            },
            settingsOnDatabase = {
                navController.navigate(Database) {
                    launchSingleTop = true
                }
            },
            settingsOnBack = {
                navController.popBackStack<Settings>(true)
            },
            settingsOnDailyGoals = {

            },
            personalizationOnBack = {
                navController.popBackStack<Personalization>(true)
            },
            personalizationOnHomePersonalization = {
                navController.navigate(HomePersonalization) {
                    launchSingleTop = true
                }
            },
            personalizationOnNutritionFactsPersonalization = {
                navController.navigate(NutritionFactsPersonalization) {
                    launchSingleTop = true
                }
            },
            homePersonalizationOnBack = {
                navController.popBackStack<HomePersonalization>(true)
            },
            homePersonalizationOnMealsSettings = {

            },
            homePersonalizationOnGoalsSettings = {

            },
            nutritionFactsPersonalizationOnBack = {
                navController.popBackStack<NutritionFactsPersonalization>(true)
            },
            externalDatabasesOnBack = {
                navController.popBackStack<ExternalDatabases>(true)
            },
            externalDatabasesOnSwissFoodCompositionDatabase = {

            },
            databaseOnBack = {
                navController.popBackStack<Database>(true)
            },
            databaseOnExternalDatabases = {
                navController.navigate(ExternalDatabases) {
                    launchSingleTop = true
                }
            }
        )
    }
}
