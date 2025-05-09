package com.maksimowiczm.foodyou.feature.meal.ui.card

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.feature.meal.data.MealCardsLayout
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MealsCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    homeState: HomeState,
    onMealClick: (epochDay: Int, mealId: Long) -> Unit,
    onAddClick: (epochDay: Int, mealId: Long) -> Unit,
    onLongClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: MealsCardViewModel = koinViewModel()
) {
    val meals by viewModel.observeMeals(homeState.selectedDate).collectAsStateWithLifecycle(null)
    val layout by viewModel.layout.collectAsStateWithLifecycle()

    when (layout) {
        MealCardsLayout.Horizontal -> HorizontalMealsCard(
            meals = meals,
            onMealClick = { onMealClick(homeState.selectedDate.toEpochDays(), it) },
            onAddClick = { onAddClick(homeState.selectedDate.toEpochDays(), it) },
            onLongClick = onLongClick,
            animatedVisibilityScope = animatedVisibilityScope,
            epochDay = homeState.selectedDate.toEpochDays(),
            contentPadding = contentPadding,
            shimmer = homeState.shimmer,
            modifier = modifier
        )

        MealCardsLayout.Vertical -> VerticalMealsCard(
            meals = meals,
            onMealClick = { onMealClick(homeState.selectedDate.toEpochDays(), it) },
            onAddClick = { onAddClick(homeState.selectedDate.toEpochDays(), it) },
            onLongClick = onLongClick,
            animatedVisibilityScope = animatedVisibilityScope,
            epochDay = homeState.selectedDate.toEpochDays(),
            contentPadding = contentPadding,
            shimmer = homeState.shimmer,
            modifier = modifier
        )
    }
}
