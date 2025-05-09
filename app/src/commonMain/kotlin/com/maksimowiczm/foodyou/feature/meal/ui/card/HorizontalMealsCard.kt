package com.maksimowiczm.foodyou.feature.meal.ui.card

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.maksimowiczm.foodyou.core.ui.LocalNavigationSharedTransitionScope
import com.maksimowiczm.foodyou.feature.meal.domain.MealWithSummary
import com.valentinilk.shimmer.Shimmer
import kotlin.math.absoluteValue

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun HorizontalMealsCard(
    meals: List<MealWithSummary>?,
    onMealClick: (mealId: Long) -> Unit,
    onAddClick: (mealId: Long) -> Unit,
    onLongClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    epochDay: Int,
    contentPadding: PaddingValues,
    shimmer: Shimmer,
    modifier: Modifier = Modifier.Companion
) = with(LocalNavigationSharedTransitionScope.current ?: error("SharedTransitionScope not found")) {
    // Must be same as meals count or more but since we don't have meals count yet set it to some
    // extreme value. If it is less than actual meals count pager will scroll back to the
    // last item which is annoying for the user.
    // Let's assume that user won't use more than 20 meals
    val pagerState = rememberPagerState(
        pageCount = { meals?.size ?: 20 }
    )

    val transition = updateTransition(meals)

    HorizontalPager(
        state = pagerState,
        modifier = modifier.animateContentSize(),
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = 24.dp,
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding()
        )
    ) { page ->
        val pageOffset = pagerState.currentPage - page + pagerState.currentPageOffsetFraction
        val fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
        val meal = meals?.getOrNull(page)

        transition.Crossfade(
            contentKey = { it != null },
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
                .scale(
                    scaleX = 1f,
                    scaleY = lerp(0.9f, 1f, fraction)
                )
        ) {
            if (it != null && meal != null) {
                MealCard(
                    animatedVisibilityScope = animatedVisibilityScope,
                    epochDay = epochDay,
                    meal = meal,
                    isEmpty = meal.isEmpty,
                    totalCalories = meal.calories,
                    totalProteins = meal.proteins,
                    totalCarbohydrates = meal.carbohydrates,
                    totalFats = meal.fats,
                    onMealClick = { onMealClick(meal.id) },
                    onAddClick = { onAddClick(meal.id) },
                    onLongClick = onLongClick
                )
            } else {
                MealCardSkeleton(
                    shimmer = shimmer
                )
            }
        }
    }
}
