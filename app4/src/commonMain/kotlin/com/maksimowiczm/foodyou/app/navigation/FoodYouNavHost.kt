package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.app.navigation.FoodYouNavHostRoutes.Home
import com.maksimowiczm.foodyou.app.ui.home.HomeScreen
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
                onLanguage = { /* TODO */ },
                onPrivacy = { /* TODO */ },
                onAbout = { /* TODO */ },
                onAddProfile = { /* TODO */ },
                onEditProfile = { /* TODO */ },
            )
        }
    }
}

private object FoodYouNavHostRoutes {

    @Serializable data object Home
}
