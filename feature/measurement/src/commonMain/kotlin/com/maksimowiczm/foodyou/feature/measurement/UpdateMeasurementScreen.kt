package com.maksimowiczm.foodyou.feature.measurement

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.model.FoodId

@Composable
fun UpdateMeasurementScreen(
    measurementId: Long,
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onRecipeClone: (FoodId.Product, mealId: Long?, epochDay: Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    com.maksimowiczm.foodyou.feature.measurement.ui.UpdateMeasurementScreen(
        measurementId = measurementId,
        onBack = onBack,
        onEditFood = onEditFood,
        onRecipeClone = onRecipeClone,
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
    )
}
