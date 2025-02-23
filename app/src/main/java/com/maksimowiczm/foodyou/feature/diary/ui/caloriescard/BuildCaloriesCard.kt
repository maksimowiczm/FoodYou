package com.maksimowiczm.foodyou.feature.diary.ui.caloriescard

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.HomeFeature
import com.maksimowiczm.foodyou.feature.diary.ui.DiaryViewModel
import org.koin.androidx.compose.koinViewModel

fun buildCaloriesCard(onClick: () -> Unit) = HomeFeature { _, modifier, homeState ->
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
                onClick = onClick
            )
        } else {
            CaloriesCardSkeleton(
                shimmerInstance = homeState.shimmer
            )
        }
    }
}
