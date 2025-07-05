package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.about.About
import com.maksimowiczm.foodyou.feature.about.Sponsor
import com.maksimowiczm.foodyou.feature.about.SponsorMessages
import com.maksimowiczm.foodyou.feature.about.aboutGraph
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.CreateProductScreen
import com.maksimowiczm.foodyou.feature.food.ui.UpdateProductScreen
import kotlinx.serialization.Serializable

@Serializable
data class Update(val productId: Long)

@Composable
fun FoodYouNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Update(1),
        modifier = modifier
    ) {
        composable("create") {
            CreateProductScreen(
                onBack = {
                },
                onCreate = { productId ->
                    navController.navigate(Update(productId.id)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Update> {
            val (productId) = it.toRoute<Update>()

            UpdateProductScreen(
                onBack = {},
                onUpdate = {
                    navController.navigate(About)
                },
                productId = FoodId.Product(productId)
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
