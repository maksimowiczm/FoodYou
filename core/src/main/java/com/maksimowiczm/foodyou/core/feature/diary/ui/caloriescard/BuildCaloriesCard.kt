package com.maksimowiczm.foodyou.core.feature.diary.ui.caloriescard

import androidx.compose.animation.Crossfade
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

    val dd = diaryDay

    Crossfade(
        targetState = dd != null,
        modifier = modifier
    ) {
        if (it && dd != null) {
            CaloriesCard(
                diaryDay = dd,
                onClick = {
                    navController.navigateToGoalsSettings(
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                }
            )
        } else {
            CaloriesCardSkeleton(
                shimmerInstance = homeState.shimmer
            )
        }
    }
}
