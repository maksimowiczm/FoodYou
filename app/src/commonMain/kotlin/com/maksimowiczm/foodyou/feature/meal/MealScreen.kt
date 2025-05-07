package com.maksimowiczm.foodyou.feature.meal

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.feature.meal.ui.screen.MealScreen
import com.maksimowiczm.foodyou.feature.meal.ui.screen.MealScreenViewModel
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * @param screenSts scope for the screen transition
 * @param enterSts scope for the enter transition
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MealScreen(
    screenSts: SharedTransitionScope,
    screenScope: AnimatedVisibilityScope,
    enterSts: SharedTransitionScope,
    enterScope: AnimatedVisibilityScope,
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
        screenSts = screenSts,
        screenScope = screenScope,
        enterSts = enterSts,
        enterScope = enterScope,
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
