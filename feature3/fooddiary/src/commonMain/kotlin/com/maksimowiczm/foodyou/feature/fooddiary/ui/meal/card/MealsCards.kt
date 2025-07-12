package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.HomeState
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrderPreference
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MealsCards(
    homeState: HomeState,
    onAdd: (epochDay: Long, mealId: Long) -> Unit,
    onEditMeasurement: (Long) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val viewModel: MealsCardsViewModel = koinViewModel()
    val orderPreference = userPreference<NutrientsOrderPreference>()
    val order = orderPreference.collectAsStateWithLifecycle(orderPreference.getBlocking()).value

    val meals = viewModel.meals.collectAsStateWithLifecycle().value

    LaunchedEffect(homeState.selectedDate, viewModel) {
        viewModel.setDate(homeState.selectedDate)
    }

    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (meals == null) {
            repeat(4) {
                MealCardSkeleton(shimmer = homeState.shimmer)
            }
        } else {
            meals.forEach { meal ->
                MealCard(
                    meal = meal,
                    order = order,
                    onAddFood = { onAdd(homeState.selectedDate.toEpochDays(), meal.id) },
                    onEditMeasurement = onEditMeasurement,
                    onDeleteEntry = viewModel::onDeleteMeasurement
                )
            }
        }
    }
}
