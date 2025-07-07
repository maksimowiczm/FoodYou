package com.maksimowiczm.foodyou.feature.fooddiary.ui.search

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.fooddiary.data.Food

@Composable
internal fun FoodSearchListItem(food: Food, modifier: Modifier = Modifier.Companion) {
    ListItem(
        headlineContent = {
            Text(food.headline)
        },
        modifier = modifier
    )
}
