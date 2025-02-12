package com.maksimowiczm.foodyou.core.feature.diary.ui.caloriescard

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.core.feature.HomeFeature
import com.maksimowiczm.foodyou.core.feature.diary.DiaryFeature.navigateToGoalsSettings
import com.maksimowiczm.foodyou.core.feature.diary.ui.DiaryViewModel
import org.koin.androidx.compose.koinViewModel

fun buildCaloriesCard(navController: NavController) = HomeFeature { modifier, homeState ->
    val viewModel = koinViewModel<DiaryViewModel>()
    val diaryDay by viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)

    diaryDay?.let {
        CaloriesCard(
            diaryDay = it,
            onClick = {
                navController.navigateToGoalsSettings(
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            },
            modifier = modifier
        )
    }
}
