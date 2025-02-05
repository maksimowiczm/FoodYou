package com.maksimowiczm.foodyou.core.feature.diary.ui.mealscard

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.core.feature.HomeFeature
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.AddFoodFeature
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.navigateToAddFood
import com.maksimowiczm.foodyou.core.feature.diary.ui.DiaryViewModel
import org.koin.androidx.compose.koinViewModel

fun buildMealsCard(navController: NavController) = HomeFeature { modifier, homeState ->
    val viewModel = koinViewModel<DiaryViewModel>()

    val diaryDay by viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)

    diaryDay?.let {
        MealsCard(
            diaryDay = it,
            onAddClick = { meal ->
                navController.navigateToAddFood(
                    route = AddFoodFeature(
                        epochDay = homeState.selectedDate.toEpochDay(),
                        meal = meal
                    ),
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            modifier = modifier
        )
    }
}
