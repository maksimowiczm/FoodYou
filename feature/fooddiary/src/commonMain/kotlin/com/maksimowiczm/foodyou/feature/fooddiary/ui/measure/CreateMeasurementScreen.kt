package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun CreateMeasurementScreen(
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onDelete: () -> Unit,
    onCreateMeasurement: () -> Unit,
    foodId: FoodId,
    mealId: Long,
    date: LocalDate,
    measurement: Measurement?,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<CreateMeasurementViewModel>(
        parameters = { parametersOf(foodId) }
    )

    val food = viewModel.food.collectAsStateWithLifecycle().value
    val foodEvents = viewModel.foodEvents?.collectAsStateWithLifecycle()?.value
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val today = viewModel.today.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value
    val selectedMeasurement = viewModel.selectedMeasurement.collectAsStateWithLifecycle().value
    val possibleTypes = viewModel.possibleMeasurementTypes.collectAsStateWithLifecycle().value

    val latestOnDelete by rememberUpdatedState(onDelete)
    val latestOnCreateMeasurement by rememberUpdatedState(onCreateMeasurement)
    LaunchedCollectWithLifecycle(viewModel.events) {
        when (it) {
            MeasurementEvent.Deleted -> latestOnDelete()
            MeasurementEvent.Saved -> latestOnCreateMeasurement()
        }
    }

    if (food == null ||
        suggestions == null ||
        selectedMeasurement == null ||
        possibleTypes == null
    ) {
        // TODO loading state
    } else {
        FoodMeasurementScreen(
            onBack = onBack,
            onEditFood = onEditFood,
            onDelete = viewModel::deleteFood,
            onMeasure = viewModel::createMeasurement,
            onUnpack = viewModel::unpack,
            food = food,
            foodEvents = foodEvents,
            today = today,
            selectedDate = date,
            meals = meals,
            selectedMeal = meals.first { it.id == mealId },
            suggestions = suggestions,
            possibleTypes = possibleTypes,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
            selectedMeasurement = measurement ?: selectedMeasurement
        )
    }
}
