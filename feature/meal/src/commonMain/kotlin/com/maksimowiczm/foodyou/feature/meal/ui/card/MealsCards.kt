package com.maksimowiczm.foodyou.feature.meal.ui.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.feature.meal.data.MealCardsLayout
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MealsCards(
    homeState: HomeState,
    onAdd: (epochDay: Int, mealId: Long) -> Unit,
    onEditMeasurement: (Long) -> Unit,
    onLongClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: MealsCardsViewModel = koinViewModel()
) {
    val layout by viewModel.layout.collectAsStateWithLifecycle()

    val meals = viewModel.meals.collectAsStateWithLifecycle().value

    LaunchedEffect(homeState.selectedDate, viewModel) {
        viewModel.setDate(homeState.selectedDate)
    }

    when (layout) {
        MealCardsLayout.Horizontal -> HorizontalMealsCards(
            meals = meals,
            onAdd = { onAdd(homeState.selectedDate.toEpochDays(), it) },
            onLongClick = onLongClick,
            shimmer = homeState.shimmer,
            contentPadding = contentPadding,
            onEditMeasurement = onEditMeasurement,
            onUnpackRecipe = viewModel::unpackRecipe,
            onDeleteEntry = viewModel::onDeleteMeasurement,
            modifier = modifier
        )

        MealCardsLayout.Vertical -> VerticalMealsCards(
            meals = meals,
            onAdd = { onAdd(homeState.selectedDate.toEpochDays(), it) },
            onLongClick = onLongClick,
            shimmer = homeState.shimmer,
            contentPadding = contentPadding,
            onEditMeasurement = onEditMeasurement,
            onUnpackRecipe = viewModel::unpackRecipe,
            onDeleteEntry = viewModel::onDeleteMeasurement,
            modifier = modifier
        )
    }
}
