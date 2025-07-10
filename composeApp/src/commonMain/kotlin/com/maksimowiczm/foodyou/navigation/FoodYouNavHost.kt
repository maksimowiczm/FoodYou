package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.feature.about.About
import com.maksimowiczm.foodyou.feature.about.Sponsor
import com.maksimowiczm.foodyou.feature.about.SponsorMessages
import com.maksimowiczm.foodyou.feature.about.aboutGraph
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import kotlinx.datetime.LocalDate

@Composable
fun FoodYouNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = FoodSearch(
            mealId = 1L,
            epochDay = LocalDate.now().toEpochDays()
        ),
        modifier = modifier
    ) {
        foodDiaryGraph(
            foodSearchOnBack = {
                navController.popBackStack<FoodSearch>(true)
            },
            foodSearchOnCreateProduct = {
                navController.navigate(CreateProduct) {
                    launchSingleTop = true
                }
            },
            foodSearchOnOpenFoodFactsProduct = {
                navController.navigate(OpenFoodFactsProduct(it)) {
                    launchSingleTop = true
                }
            },
            foodSearchOnFood = {
                // TODO
                val route = when (it) {
                    is FoodId.Product -> UpdateProduct.from(it)
                    is FoodId.Recipe -> TODO()
                }

                navController.navigate(route) {
                    launchSingleTop = true
                }
            },
            openFoodFactsProductOnBack = {
                navController.popBackStack<OpenFoodFactsProduct>(true)
            },
            openFoodFactsProductOnImport = {
            },
            createProductOnBack = {
                navController.popBackStack<CreateProduct>(true)
            },
            createProductOnCreate = {
                // TODO
            },
            updateProductOnBack = {
                navController.popBackStack<UpdateProduct>(true)
            },
            updateProductOnUpdate = {
                navController.popBackStack<UpdateProduct>(true)
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
    }
}
