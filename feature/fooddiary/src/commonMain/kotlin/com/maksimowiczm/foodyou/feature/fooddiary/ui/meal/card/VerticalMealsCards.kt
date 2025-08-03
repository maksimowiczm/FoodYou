package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrderPreference
import com.maksimowiczm.foodyou.feature.fooddiary.domain.Meal
import com.valentinilk.shimmer.Shimmer

@Composable
internal fun VerticalMealsCards(
    meals: List<Meal>?,
    onAdd: (mealId: Long) -> Unit,
    onEditMeasurement: (Long) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onLongClick: (mealId: Long) -> Unit,
    shimmer: Shimmer,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val orderPreference = userPreference<NutrientsOrderPreference>()
    val order = orderPreference.collectAsStateWithLifecycle(orderPreference.getBlocking()).value

    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (meals == null) {
            repeat(4) {
                MealCardSkeleton(shimmer)
            }
        } else {
            meals.forEach { meal ->
                MealCard(
                    meal = meal,
                    order = order,
                    onAddFood = { onAdd(meal.id) },
                    onEditMeasurement = onEditMeasurement,
                    onDeleteEntry = onDeleteEntry,
                    onLongClick = { onLongClick(meal.id) }
                )
            }
        }
    }
}
