package com.maksimowiczm.foodyou.feature.meal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.ui.home.HomeState

@Composable
fun MealsCards(
    homeState: HomeState,
    onAdd: (epochDay: Int, mealId: Long) -> Unit,
    onEditMeasurement: (MeasurementId) -> Unit,
    onLongClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    com.maksimowiczm.foodyou.feature.meal.ui.card.MealsCards(
        homeState = homeState,
        onAdd = onAdd,
        onEditMeasurement = onEditMeasurement,
        onLongClick = onLongClick,
        contentPadding = contentPadding,
        modifier = modifier
    )
}
