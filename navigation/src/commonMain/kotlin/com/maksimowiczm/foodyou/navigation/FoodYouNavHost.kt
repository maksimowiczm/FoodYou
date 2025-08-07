package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.navigation.domain.AboutDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorMessagesDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsLanguageDestination
import com.maksimowiczm.foodyou.navigation.domain.SettingsMealsDestination
import com.maksimowiczm.foodyou.navigation.graph.about.aboutNavigationGraph
import com.maksimowiczm.foodyou.navigation.graph.settings.settingsNavigationGraph

@Composable
fun FoodYouNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsDestination,
        modifier = modifier,
    ) {
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
            mealsOnBack = { navController.popBackStack<SettingsMealsDestination>(true) },
            mealsOnMealsCardsSettings = {
                // TODO
            },
            languageOnBack = { navController.popBackStack<SettingsLanguageDestination>(true) },
        )
    }
}

private fun <T : Any> NavController.navigateSingleTop(route: T) =
    navigate(route) { launchSingleTop = true }
