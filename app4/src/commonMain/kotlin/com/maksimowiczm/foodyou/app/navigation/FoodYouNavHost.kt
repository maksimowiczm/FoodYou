package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoutes.About
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoutes.Home
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoutes.Language
import com.maksimowiczm.foodyou.app.ui.about.AboutScreen
import com.maksimowiczm.foodyou.app.ui.home.HomeScreen
import com.maksimowiczm.foodyou.app.ui.language.LanguageScreen
import kotlinx.serialization.Serializable

@Composable
fun FoodYouNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(modifier = modifier, navController = navController, startDestination = Home) {
        forwardBackwardComposable<Home> {
            HomeScreen(
                onFoodDatabase = { /* TODO */ },
                onPersonalization = { /* TODO */ },
                onDataBackupAndExport = { /* TODO */ },
                onLanguage = { navController.navigateSingleTop(Language) },
                onPrivacy = { /* TODO */ },
                onAbout = { navController.navigateSingleTop(About) },
                onAddProfile = { /* TODO */ },
                onEditProfile = { /* TODO */ },
            )
        }
        forwardBackwardComposable<About> {
            AboutScreen(onBack = { navController.popBackStackInclusive<About>() })
        }
        forwardBackwardComposable<Language> {
            LanguageScreen(onBack = { navController.popBackStackInclusive<Language>() })
        }
    }
}

private object FoodYouNavHostRoutes {

    @Serializable data object Home

    @Serializable data object About

    @Serializable data object Language
}
