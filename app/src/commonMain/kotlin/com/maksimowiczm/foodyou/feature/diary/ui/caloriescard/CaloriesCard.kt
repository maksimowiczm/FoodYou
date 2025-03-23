package com.maksimowiczm.foodyou.feature.diary.ui.caloriescard

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.HomeState
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.ui.CaloriesIndicatorTransitionKeys
import com.maksimowiczm.foodyou.feature.diary.ui.component.CaloriesIndicator
import com.maksimowiczm.foodyou.feature.diary.ui.component.CaloriesIndicatorLegend
import com.maksimowiczm.foodyou.feature.diary.ui.component.CaloriesIndicatorSkeleton
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientIndicatorLegendSkeleton
import com.maksimowiczm.foodyou.ui.LocalHomeSharedTransitionScope
import com.maksimowiczm.foodyou.ui.home.FoodYouHomeCard
import com.valentinilk.shimmer.Shimmer
import kotlin.math.roundToInt
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CaloriesCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeState: HomeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CaloriesCardViewModel = koinViewModel()
) {
    val diaryDayState = viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)
    val diaryDay = diaryDayState.value

    val homeSTS =
        LocalHomeSharedTransitionScope.current ?: error("No HomeSharedTransitionScope provided")

    Crossfade(
        // Update only when null state changes
        targetState = diaryDay != null,
        modifier = modifier
    ) {
        if (it && diaryDay != null) {
            with(homeSTS) {
                CaloriesCard(
                    animatedVisibilityScope = animatedVisibilityScope,
                    diaryDay = diaryDay,
                    onClick = onClick
                )
            }
        } else {
            CaloriesCardSkeleton(
                onClick = onClick,
                shimmer = homeState.shimmer
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CaloriesCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    diaryDay: DiaryDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FoodYouHomeCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CaloriesIndicator(
                calories = diaryDay.totalCalories.roundToInt(),
                caloriesGoal = diaryDay.dailyGoals.calories,
                proteins = diaryDay.totalProteins.roundToInt(),
                carbohydrates = diaryDay.totalCarbohydrates.roundToInt(),
                fats = diaryDay.totalFats.roundToInt(),
                modifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(
                        key = CaloriesIndicatorTransitionKeys.CaloriesIndicator(
                            epochDay = diaryDay.date.toEpochDays()
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )

            CaloriesIndicatorLegend(
                proteins = diaryDay.totalProteins.roundToInt(),
                proteinsGoal = diaryDay.dailyGoals.proteinsAsGrams.roundToInt(),
                carbohydrates = diaryDay.totalCarbohydrates.roundToInt(),
                carbohydratesGoal = diaryDay.dailyGoals.carbohydratesAsGrams.roundToInt(),
                fats = diaryDay.totalFats.roundToInt(),
                fatsGoal = diaryDay.dailyGoals.fatsAsGrams.roundToInt()
            )
        }
    }
}

@Composable
private fun CaloriesCardSkeleton(
    onClick: () -> Unit,
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    FoodYouHomeCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CaloriesIndicatorSkeleton(shimmer)
            NutrientIndicatorLegendSkeleton(shimmer)
        }
    }
}
