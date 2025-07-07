package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.feature.about.About
import com.maksimowiczm.foodyou.feature.about.Sponsor
import com.maksimowiczm.foodyou.feature.about.SponsorMessages
import com.maksimowiczm.foodyou.feature.about.aboutGraph
import com.maksimowiczm.foodyou.feature.food.ui.CreateProductScreen
import com.maksimowiczm.foodyou.feature.fooddiary.ui.FoodSearchScreen
import kotlinx.datetime.LocalDate

@Composable
fun FoodYouNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "search",
        modifier = modifier
    ) {
        composable("search") {
            FoodSearchScreen(
                mealId = 1L,
                date = LocalDate.now(),
                onBack = {
                    navController.popBackStack("search", true)
                },
                onCreateProduct = {
                    navController.navigate("create") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("create") {
            CreateProductScreen(
                onBack = {
                    navController.popBackStack("create", true)
                },
                onCreate = {
                    navController.popBackStack("create", true)
                }
            )
        }

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
