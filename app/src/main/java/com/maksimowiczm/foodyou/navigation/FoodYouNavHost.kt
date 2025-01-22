package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.addfood.navigation.AddFoodRoute
import com.maksimowiczm.foodyou.feature.addfood.navigation.addFoodGraph
import com.maksimowiczm.foodyou.feature.addfood.navigation.navigateToAddFood
import com.maksimowiczm.foodyou.feature.diary.navigation.DiaryFeature
import com.maksimowiczm.foodyou.feature.diary.navigation.diaryGraph

@Composable
fun FoodYouNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = DiaryFeature
    ) {
        diaryGraph(
            onAddProductToMeal = { meal, date ->
                navController.navigateToAddFood(
                    route = AddFoodRoute.Search(
                        meal = meal,
                        epochDay = date.toEpochDay()
                    ),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        )
        addFoodGraph(
            searchOnCloseClick = {
                navController.popBackStack<AddFoodRoute.Search>(inclusive = true)
            },
            searchOnProductClick = { productId, meal, date ->
                navController.navigateToAddFood(
                    route = AddFoodRoute.CreatePortion(
                        productId = productId,
                        meal = meal,
                        epochDay = date.toEpochDay()
                    )
                )
            },
            createOnSuccess = {
                navController.popBackStack<AddFoodRoute.CreatePortion>(inclusive = true)
            },
            createOnNavigateBack = {
                navController.popBackStack<AddFoodRoute.CreatePortion>(inclusive = true)
            }
        )
    }
}
