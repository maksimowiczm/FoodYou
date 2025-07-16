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
import com.maksimowiczm.foodyou.feature.food.domain.FoodId

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
            homeOnSettings = {
                navController.navigate(Settings) {
                    launchSingleTop = true
                }
            },
            homeOnAbout = {
                navController.navigate(About) {
                    launchSingleTop = true
                }
            },
            homeMealCardOnAdd = { epochDay, mealId ->
                navController.navigate(
                    FoodSearch(
                        mealId = mealId,
                        epochDay = epochDay
                    )
                ) {
                    launchSingleTop = true
                }
            },
            homeMealCardOnEditMeasurement = {
                navController.navigate(UpdateProductMeasurement(it)) {
                    launchSingleTop = true
                }
            },
            settingsOnBack = {
                navController.popBackStack<Settings>(true)
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
            }
        )
        foodDiaryGraph(
            foodSearchOnBack = {
                navController.popBackStack<FoodSearch>(true)
            },
            foodSearchOnCreateProduct = { mealId, date ->
                navController.navigate(
                    CreateProduct(
                        mealId = mealId,
                        date = date
                    )
                ) {
                    launchSingleTop = true
                }
            },
            foodSearchOnFood = { id, measurement, mealId, date ->
                val route = when (id) {
                    is FoodId.Product -> CreateProductMeasurement(
                        foodId = id,
                        mealId = mealId,
                        date = date,
                        measurement = measurement
                    )

                    is FoodId.Recipe -> TODO()
                }

                navController.navigate(route) {
                    launchSingleTop = true
                }
            },
            createProductOnBack = {
                navController.popBackStack<CreateProduct>(true)
            },
            createProductOnCreate = { id, mealId, date ->
                navController.navigate(
                    CreateProductMeasurement(
                        foodId = id,
                        mealId = mealId,
                        date = date,
                        measurement = null
                    )
                ) {
                    launchSingleTop = true

                    popUpTo<CreateProduct> {
                        inclusive = true
                    }
                }
            },
            updateProductOnBack = {
                navController.popBackStack<UpdateProduct>(true)
            },
            updateProductOnUpdate = {
                navController.popBackStack<UpdateProduct>(true)
            },
            createMeasurementOnBack = {
                navController.popBackStack<CreateProductMeasurement>(true)
            },
            createMeasurementOnEditProduct = {
                navController.navigate(UpdateProduct.from(it)) {
                    launchSingleTop = true
                }
            },
            createMeasurementOnDeleteProduct = {
                navController.popBackStack<CreateProductMeasurement>(true)
            },
            createMeasurementOnCreateMeasurement = {
                navController.popBackStack<CreateProductMeasurement>(true)
            },
            updateMeasurementOnBack = {
                navController.popBackStack<UpdateProductMeasurement>(true)
            },
            updateMeasurementOnEdit = {
                when (it) {
                    is FoodId.Product -> navController.navigate(UpdateProduct.from(it)) {
                        launchSingleTop = true
                    }

                    is FoodId.Recipe -> TODO()
                }
            },
            updateMeasurementOnDelete = {
                navController.popBackStack<UpdateProductMeasurement>(true)
            },
            updateMeasurementOnUpdate = {
                navController.popBackStack<UpdateProductMeasurement>(true)
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
