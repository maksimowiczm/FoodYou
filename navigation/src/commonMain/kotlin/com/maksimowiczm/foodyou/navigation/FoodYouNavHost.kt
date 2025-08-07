package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.navigation.domain.AboutDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorMessagesDestination
import com.maksimowiczm.foodyou.navigation.graph.about.aboutNavigationGraph

@Composable
fun FoodYouNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AboutDestination,
        modifier = modifier,
    ) {
        aboutNavigationGraph(
            masterOnBack = { navController.popBackStack<AboutDestination>(true) },
            masterOnSponsor = { navController.navigate(AboutSponsorMessagesDestination) },
            sponsorMessagesOnBack = {
                navController.popBackStack<AboutSponsorMessagesDestination>(true)
            },
            sponsorMessagesOnSponsor = { navController.navigate(AboutSponsorDestination) },
            sponsorOnBack = { navController.popBackStack<AboutSponsorDestination>(true) },
        )
    }
}
