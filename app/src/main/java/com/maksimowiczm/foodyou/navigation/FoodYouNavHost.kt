package com.maksimowiczm.foodyou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
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
            onAddProductMeal = {}
        )
    }
}
