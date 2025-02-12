package com.maksimowiczm.foodyou.core.feature.diary.ui.mealscard

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.core.feature.HomeFeature
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.AddFoodFeature
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.navigateToAddFood
import com.maksimowiczm.foodyou.core.feature.diary.DiaryFeature.navigateToMealsSettings
import org.koin.androidx.compose.koinViewModel

fun buildMealsCard(navController: NavController) = HomeFeature { modifier, homeState ->
    val viewModel = koinViewModel<MealsCardViewModel>()

    val diaryDay by viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)

    val time by viewModel.time.collectAsStateWithLifecycle()

    diaryDay?.let {
        MealsCard(
            diaryDay = it,
            time = time,
            onAddClick = { meal ->
                navController.navigateToAddFood(
                    route = AddFoodFeature(
                        epochDay = homeState.selectedDate.toEpochDays(),
                        mealId = meal.id
                    ),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            onEditMeals = {
                navController.navigateToMealsSettings(
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            modifier = modifier
        )
    }
}
