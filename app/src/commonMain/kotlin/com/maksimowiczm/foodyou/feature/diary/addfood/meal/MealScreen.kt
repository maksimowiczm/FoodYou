package com.maksimowiczm.foodyou.feature.diary.addfood.meal

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.diary.addfood.meal.ui.MealScreen
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementId
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
