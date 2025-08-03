package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.HomeState
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.MealsCardsLayout
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.MealsCardsLayoutPreference
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MealsCards(
    homeState: HomeState,
    onAdd: (epochDay: Long, mealId: Long) -> Unit,
    onEditMeasurement: (Long) -> Unit,
    onLongClick: (mealId: Long) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val viewModel: MealsCardsViewModel = koinViewModel()
    val layoutPreference: MealsCardsLayoutPreference = userPreference()

    val layout = layoutPreference.collectAsStateWithLifecycle(layoutPreference.getBlocking()).value

    val meals = viewModel.meals.collectAsStateWithLifecycle().value

    LaunchedEffect(homeState.selectedDate, viewModel) {
        viewModel.setDate(homeState.selectedDate)
    }

    when (layout) {
        MealsCardsLayout.Horizontal -> HorizontalMealsCards(
            meals = meals,
            onAdd = { mealId -> onAdd(homeState.selectedDate.toEpochDays(), mealId) },
            onEditMeasurement = onEditMeasurement,
            onDeleteEntry = viewModel::onDeleteMeasurement,
            onLongClick = onLongClick,
            shimmer = homeState.shimmer,
            contentPadding = contentPadding,
            modifier = modifier
        )

        MealsCardsLayout.Vertical -> VerticalMealsCards(
            meals = meals,
            onAdd = { mealId -> onAdd(homeState.selectedDate.toEpochDays(), mealId) },
            onEditMeasurement = onEditMeasurement,
            onDeleteEntry = viewModel::onDeleteMeasurement,
            onLongClick = onLongClick,
            shimmer = homeState.shimmer,
            contentPadding = contentPadding,
            modifier = modifier
        )
    }
}
