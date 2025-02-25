package com.maksimowiczm.foodyou.feature.diary.ui.mealscard

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.HomeFeature
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import org.koin.androidx.compose.koinViewModel

fun buildMealsCard(
    onMealClick: (epochDay: Int, meal: Meal) -> Unit,
    onAddClick: (epochDay: Int, meal: Meal) -> Unit
) = HomeFeature(
    applyPadding = false
) { animatedVisibilityScope, modifier, homeState ->
    val viewModel = koinViewModel<MealsCardViewModel>()
    val diaryDay by viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)

    val time by viewModel.time.collectAsStateWithLifecycle()

    MealsCard(
        animatedVisibilityScope = animatedVisibilityScope,
        state = rememberMealsCardState(
            diaryDay = diaryDay,
            time = time,
            shimmer = homeState.shimmer
        ),
        formatTime = viewModel::formatTime,
        onMealClick = { onMealClick(homeState.selectedDate.toEpochDays(), it) },
        onAddClick = { onAddClick(homeState.selectedDate.toEpochDays(), it) },
        modifier = modifier
    )
}
