package com.maksimowiczm.foodyou.feature.meal.ui.card

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.LocalNavigationSharedTransitionScope
import com.maksimowiczm.foodyou.feature.meal.domain.MealWithSummary
import com.valentinilk.shimmer.Shimmer

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun VerticalMealsCard(
    meals: List<MealWithSummary>?,
    onMealClick: (mealId: Long) -> Unit,
    onAddClick: (mealId: Long) -> Unit,
    onLongClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    epochDay: Int,
    contentPadding: PaddingValues,
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) = with(LocalNavigationSharedTransitionScope.current ?: error("SharedTransitionScope not found")) {
    val transition = updateTransition(meals)

    val meals = remember(meals) {
        meals ?: List<MealWithSummary?>(4) { null }
    }

    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        meals.forEachIndexed { i, meal ->
            transition.Crossfade(
                contentKey = { it != null },
                modifier = Modifier.fillMaxWidth()
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
                    MealCardSkeleton(shimmer)
                }
            }
        }
    }
}
