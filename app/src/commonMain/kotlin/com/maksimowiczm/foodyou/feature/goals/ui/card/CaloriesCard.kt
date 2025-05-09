package com.maksimowiczm.foodyou.feature.goals.ui.card

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.LocalNavigationSharedTransitionScope
import com.maksimowiczm.foodyou.core.ui.home.FoodYouHomeCard
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.feature.goals.model.DiaryDay
import com.maksimowiczm.foodyou.feature.goals.ui.CaloriesIndicatorTransitionKeys
import com.maksimowiczm.foodyou.feature.goals.ui.component.CaloriesIndicator
import com.maksimowiczm.foodyou.feature.goals.ui.component.CaloriesIndicatorLegend
import com.maksimowiczm.foodyou.feature.goals.ui.component.CaloriesIndicatorSkeleton
import com.maksimowiczm.foodyou.feature.goals.ui.component.NutrientIndicatorLegendSkeleton
import com.valentinilk.shimmer.Shimmer
import kotlin.math.roundToInt
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun CaloriesCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeState: HomeState,
    onClick: (epochDay: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CaloriesCardViewModel = koinViewModel()
) {
    val diaryDayState = viewModel
        .observeDiaryDay(homeState.selectedDate)
        .collectAsStateWithLifecycle(null)
    val diaryDay = diaryDayState.value

    val homeSTS = LocalNavigationSharedTransitionScope.current
        ?: error("No HomeSharedTransitionScope provided")

    val onClick = remember(homeState, onClick) {
        { onClick(diaryDay?.date?.toEpochDays() ?: homeState.selectedDate.toEpochDays()) }
    }

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
                calories = diaryDay.totalCalories,
                caloriesGoal = diaryDay.dailyGoals.calories,
                proteins = diaryDay.totalProteins,
                carbohydrates = diaryDay.totalCarbohydrates,
                fats = diaryDay.totalFats,
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
                proteins = diaryDay.totalProteins,
                proteinsGoal = diaryDay.dailyGoals.proteinsAsGrams.roundToInt(),
                carbohydrates = diaryDay.totalCarbohydrates,
                carbohydratesGoal = diaryDay.dailyGoals.carbohydratesAsGrams.roundToInt(),
                fats = diaryDay.totalFats,
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
