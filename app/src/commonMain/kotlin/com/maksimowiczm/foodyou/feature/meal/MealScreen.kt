package com.maksimowiczm.foodyou.feature.meal

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.feature.meal.ui.screen.MealScreen
import com.maksimowiczm.foodyou.feature.meal.ui.screen.MealScreenViewModel
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MealScreen(
    navigationScope: AnimatedVisibilityScope,
    mealHeaderScope: AnimatedVisibilityScope,
    mealId: Long,
    date: LocalDate,
    onAddFood: () -> Unit,
    onBarcodeScanner: () -> Unit,
    onEditMeasurement: (MeasurementId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealScreenViewModel = koinViewModel(
        parameters = { parametersOf(mealId, date) }
    )
) {
    val meal = viewModel.meal.collectAsStateWithLifecycle().value
    val foods = viewModel.foods.collectAsStateWithLifecycle().value

    MealScreen(
        date = date,
        meal = meal,
        foods = foods,
        onAddFood = onAddFood,
        onBarcodeScanner = onBarcodeScanner,
        onEditMeasurement = onEditMeasurement,
        onDeleteEntry = viewModel::onDeleteMeasurement,
        modifier = modifier
    )
}
