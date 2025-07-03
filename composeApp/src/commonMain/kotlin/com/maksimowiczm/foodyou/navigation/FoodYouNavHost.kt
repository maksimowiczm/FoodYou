package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.feature.about.About
import com.maksimowiczm.foodyou.feature.about.Sponsor
import com.maksimowiczm.foodyou.feature.about.SponsorMessages
import com.maksimowiczm.foodyou.feature.about.aboutGraph

@Composable
fun FoodYouNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = About,
        modifier = modifier
    ) {
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
    }
}
