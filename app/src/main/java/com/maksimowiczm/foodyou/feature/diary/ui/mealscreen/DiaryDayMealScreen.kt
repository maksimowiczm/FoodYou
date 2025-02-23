package com.maksimowiczm.foodyou.feature.diary.ui.mealscreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.ui.SharedTransitionKeys
import com.maksimowiczm.foodyou.ui.LocalSharedTransitionScope
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DiaryDayMealScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: DiaryDayMealViewModel = koinViewModel()
) {
    val diaryDay by viewModel.observeDiaryDay(viewModel.date).collectAsStateWithLifecycle(null)

    val meal = diaryDay?.meals?.first {
        it.id == viewModel.mealId
    }

    with(LocalSharedTransitionScope.current ?: error("No SharedTransitionScope found")) {
        DiaryDayMealScreen(
            animatedVisibilityScope = animatedVisibilityScope,
            meal = meal ?: return,
            epochDay = viewModel.date.toEpochDays(),
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.DiaryDayMealScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    epochDay: Int,
    meal: Meal,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Red
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            Text(
                text = meal.name,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = SharedTransitionKeys.Meal.Title(
                            id = meal.id,
                            epochDay = epochDay
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )
        }
    }
}
