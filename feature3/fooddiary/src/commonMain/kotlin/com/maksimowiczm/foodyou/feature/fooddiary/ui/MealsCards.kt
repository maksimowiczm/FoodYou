package com.maksimowiczm.foodyou.feature.fooddiary.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.HomeState
import com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.card.MealsCards

@Composable
fun MealsCards(
    homeState: HomeState,
    onAdd: (epochDay: Long, mealId: Long) -> Unit,
    onEditMeasurement: (Long) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    MealsCards(
        homeState = homeState,
        onAdd = onAdd,
        onEditMeasurement = onEditMeasurement,
        contentPadding = contentPadding,
        modifier = modifier
    )
}
