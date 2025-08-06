package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.feature.about.About
import com.maksimowiczm.foodyou.feature.about.Sponsor
import com.maksimowiczm.foodyou.feature.about.SponsorMessages
import com.maksimowiczm.foodyou.feature.about.aboutGraph
//import com.maksimowiczm.foodyou.feature.food.domain.FoodId

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
                navController.navigate(About) {
                    launchSingleTop = true
                }
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
                navController.navigate(Language) {
                    launchSingleTop = true
                }
            },
            settingsOnSponsor = {
                navController.navigate(SponsorMessages) {
                    launchSingleTop = true
                }
            },
            settingsOnAbout = {
                navController.navigate(About) {
                    launchSingleTop = true
                }
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
        aboutGraph(
            aboutOnBack = {
                navController.popBackStack<About>(true)
            },
            aboutOnSponsor = {
                navController.navigate(SponsorMessages) {
                    launchSingleTop = true
                }
            },
            sponsorMessagesOnBack = {
                navController.popBackStack<SponsorMessages>(true)
            },
            sponsorMessagesOnSponsor = {
                navController.navigate(Sponsor) {
                    launchSingleTop = true
                }
            },
            sponsorOnBack = {
                navController.popBackStack<Sponsor>(true)
            }
        )
        languageGraph(
            onBack = {
                navController.popBackStack<Language>(true)
            }
        )
    }
}
