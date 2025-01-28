package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.addfood.navigation.AddFoodFeature
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
                    route = AddFoodFeature(
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
            onClose = {
                navController.popBackStack()
            }
        )
    }
}
