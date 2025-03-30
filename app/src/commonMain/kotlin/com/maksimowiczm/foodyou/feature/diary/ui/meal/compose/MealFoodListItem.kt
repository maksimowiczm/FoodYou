package com.maksimowiczm.foodyou.feature.diary.ui.meal.compose

import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.feature.diary.ui.component.SearchModelListItem
import com.maksimowiczm.foodyou.feature.diary.ui.meal.model.MealFoodListItem

@Composable
fun MealFoodListItem.MealFoodListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    SearchModelListItem(
        name = name,
        brand = brand,
        calories = calories,
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        weight = weight,
        measurement = weightMeasurement,
        modifier = modifier,
        onClick = onClick,
        trailingContent = null,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}
