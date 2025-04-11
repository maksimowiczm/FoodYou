package com.maksimowiczm.foodyou.feature.meal

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.feature.meal.ui.MealsCard

@Composable
fun MealsCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeState: HomeState,
    onMealClick: (epochDay: Int, mealId: Long) -> Unit,
    onAddClick: (epochDay: Int, mealId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    MealsCard(
        animatedVisibilityScope = animatedVisibilityScope,
        homeState = homeState,
        onMealClick = onMealClick,
        onAddClick = onAddClick,
        modifier = modifier
    )
}
