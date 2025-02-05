package com.maksimowiczm.foodyou.core.feature.diary.ui.caloriescard

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.feature.HomeFeature
import org.koin.androidx.compose.koinViewModel

fun buildCaloriesCard() = HomeFeature { modifier, homeState ->
    val viewModel = koinViewModel<CaloriesCardViewModel>()
    val diaryDay by viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)

    val expanded by viewModel.expanded.collectAsStateWithLifecycle()

    diaryDay?.let {
        CaloriesCard(
            diaryDay = it,
            modifier = modifier,
            expanded = expanded,
            onExpandedChange = viewModel::onExpandedChange
        )
    }
}
