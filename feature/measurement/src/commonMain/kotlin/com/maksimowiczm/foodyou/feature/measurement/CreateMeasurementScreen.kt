package com.maksimowiczm.foodyou.feature.measurement

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.model.FoodId
import kotlinx.datetime.LocalDate

@Composable
fun CreateMeasurementScreen(
    foodId: FoodId,
    mealId: Long?,
    date: LocalDate?,
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onRecipeClone: (FoodId.Product, mealId: Long?, epochDay: Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    com.maksimowiczm.foodyou.feature.measurement.ui.CreateMeasurementScreen(
        foodId = foodId,
        mealId = mealId,
        date = date,
        onBack = onBack,
        onEditFood = onEditFood,
        onRecipeClone = onRecipeClone,
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
    )
}
