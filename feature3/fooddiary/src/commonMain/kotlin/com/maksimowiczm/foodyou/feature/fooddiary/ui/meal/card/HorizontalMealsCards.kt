package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.card

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrderPreference
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Meal
import com.valentinilk.shimmer.Shimmer

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun HorizontalMealsCards(
    meals: List<Meal>?,
    onAdd: (mealId: Long) -> Unit,
    onEditMeasurement: (Long) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onLongClick: (mealId: Long) -> Unit,
    shimmer: Shimmer,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val orderPreference = userPreference<NutrientsOrderPreference>()
    val order = orderPreference.collectAsStateWithLifecycle(orderPreference.getBlocking()).value

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
        modifier = modifier.animateContentSize(
            animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
        ),
        verticalAlignment = Alignment.Top,
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = 24.dp,
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding()
        )
    ) { page ->
        val meal = meals?.getOrNull(page)

        transition.Crossfade(
            contentKey = { it != null },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
        ) {
            if (it != null && meal != null) {
                MealCard(
                    meal = meal,
                    order = order,
                    onAddFood = { onAdd(meal.id) },
                    onEditMeasurement = onEditMeasurement,
                    onDeleteEntry = onDeleteEntry,
                    onLongClick = { onLongClick(meal.id) }
                )
            } else {
                MealCardSkeleton(
                    shimmer = shimmer
                )
            }
        }
    }
}
