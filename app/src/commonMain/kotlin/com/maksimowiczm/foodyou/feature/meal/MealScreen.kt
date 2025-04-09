package com.maksimowiczm.foodyou.feature.meal

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.model.MeasurementId
import com.maksimowiczm.foodyou.feature.meal.ui.MealScreen
import kotlinx.datetime.LocalDate

@Composable
fun MealScreen(
    navigationScope: AnimatedVisibilityScope,
    mealHeaderScope: AnimatedVisibilityScope,
    mealId: Long,
    date: LocalDate,
    onAddFood: () -> Unit,
    onBarcodeScanner: () -> Unit,
    onEditEntry: (MeasurementId) -> Unit,
    modifier: Modifier = Modifier
) {
    MealScreen(
        navigationScope = navigationScope,
        mealHeaderScope = mealHeaderScope,
        mealId = mealId,
        date = date,
        onAddFood = onAddFood,
        onBarcodeScanner = onBarcodeScanner,
        onEditEntry = onEditEntry,
        modifier = modifier
    )
}
