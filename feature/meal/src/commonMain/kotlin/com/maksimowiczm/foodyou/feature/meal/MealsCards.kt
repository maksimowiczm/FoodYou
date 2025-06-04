package com.maksimowiczm.foodyou.feature.meal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ui.home.HomeState
import com.maksimowiczm.foodyou.feature.meal.ui.card.MealsCards

@Composable
fun MealsCards(
    homeState: HomeState,
    onAdd: (epochDay: Int, mealId: Long) -> Unit,
    onEditMeasurement: (measurementId: Long) -> Unit,
    onLongClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    MealsCards(
        homeState = homeState,
        onAdd = onAdd,
        onEditMeasurement = onEditMeasurement,
        onLongClick = onLongClick,
        contentPadding = contentPadding,
        modifier = modifier
    )
}
