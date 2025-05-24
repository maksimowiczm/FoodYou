package com.maksimowiczm.foodyou.feature.mealredesign.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.meal.ui.card.MealCardSkeleton
import com.maksimowiczm.foodyou.feature.mealredesign.domain.Meal
import com.valentinilk.shimmer.Shimmer

@Composable
internal fun VerticalMealsCards(
    meals: List<Meal>?,
    onAdd: (mealId: Long) -> Unit,
    onLongClick: () -> Unit,
    shimmer: Shimmer,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (meals == null) {
            repeat(4) {
                MealCardSkeleton(shimmer = shimmer)
            }
        } else {
            meals.forEach { meal ->
                MealCard(
                    meal = remember { derivedStateOf { meal } }.value,
                    onAddFood = { onAdd(meal.id) },
                    onLongClick = onLongClick
                )
            }
        }
    }
}
