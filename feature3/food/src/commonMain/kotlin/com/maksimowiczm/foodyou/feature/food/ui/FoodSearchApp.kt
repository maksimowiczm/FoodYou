package com.maksimowiczm.foodyou.feature.food.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearch
import com.maksimowiczm.foodyou.feature.food.ui.search.FoodSearchApp
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement

@Composable
fun FoodSearchApp(
    onFoodClick: (FoodSearch, Measurement) -> Unit,
    onCreateProduct: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    FoodSearchApp(
        onFoodClick = onFoodClick,
        onCreateProduct = onCreateProduct,
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
    )
}
