package com.maksimowiczm.foodyou.feature.home.meals.card

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsCardsLayout
import com.maksimowiczm.foodyou.feature.home.shared.HomeState
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MealsCards(
    homeState: HomeState,
    onAdd: (epochDay: Long, mealId: Long) -> Unit,
    onQuickAdd: (epochDay: Long, mealId: Long) -> Unit,
    onEditEntry: (foodEntryId: Long?, manualEntryId: Long?) -> Unit,
    onLongClick: (mealId: Long) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val viewModel: MealsCardsViewModel = koinViewModel()
    val diaryMeals = viewModel.diaryMeals.collectAsStateWithLifecycle().value
    val layout by viewModel.layout.collectAsStateWithLifecycle()

    LaunchedEffect(homeState.selectedDate, viewModel) { viewModel.setDate(homeState.selectedDate) }

    when (layout) {
        MealsCardsLayout.Horizontal ->
            HorizontalMealsCards(
                meals = diaryMeals,
                onAdd = { mealId -> onAdd(homeState.selectedDate.toEpochDays(), mealId) },
                onQuickAdd = { mealId -> onQuickAdd(homeState.selectedDate.toEpochDays(), mealId) },
                onEditEntry = { model ->
                    val foodEntry = model as? FoodMealEntryModel
                    val manualEntry = model as? ManualMealEntryModel
                    onEditEntry(foodEntry?.id?.value, manualEntry?.id?.value)
                },
                onDeleteEntry = viewModel::onDeleteEntry,
                onLongClick = onLongClick,
                shimmer = homeState.shimmer,
                contentPadding = contentPadding,
                modifier = modifier,
            )

        MealsCardsLayout.Vertical ->
            VerticalMealsCards(
                meals = diaryMeals,
                onAdd = { mealId -> onAdd(homeState.selectedDate.toEpochDays(), mealId) },
                onQuickAdd = { mealId -> onQuickAdd(homeState.selectedDate.toEpochDays(), mealId) },
                onEditEntry = { model ->
                    val foodEntry = model as? FoodMealEntryModel
                    val manualEntry = model as? ManualMealEntryModel
                    onEditEntry(foodEntry?.id?.value, manualEntry?.id?.value)
                },
                onDeleteEntry = viewModel::onDeleteEntry,
                onLongClick = onLongClick,
                shimmer = homeState.shimmer,
                contentPadding = contentPadding,
                modifier = modifier,
            )
    }
}
