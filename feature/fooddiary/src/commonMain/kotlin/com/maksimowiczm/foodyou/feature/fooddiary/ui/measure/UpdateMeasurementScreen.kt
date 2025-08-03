package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateMeasurementScreen(
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onDelete: () -> Unit,
    onUpdateMeasurement: () -> Unit,
    measurementId: Long,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier.Companion
) {
    val viewModel = koinViewModel<UpdateMeasurementViewModel>(
        parameters = { parametersOf(measurementId) }
    )

    val food = viewModel.food.collectAsStateWithLifecycle().value
    val productEvents = viewModel.foodEvents.collectAsStateWithLifecycle().value
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val mealId = viewModel.mealId.collectAsStateWithLifecycle().value
    val today = viewModel.today.collectAsStateWithLifecycle().value
    val measurementDate = viewModel.measurementDate.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value
    val selectedMeasurement = viewModel.measurement.collectAsStateWithLifecycle().value
    val possibleTypes = viewModel.possibleMeasurementTypes.collectAsStateWithLifecycle().value

    val latestOnDelete by rememberUpdatedState(onDelete)
    val latestOnUpdateMeasurement by rememberUpdatedState(onUpdateMeasurement)
    LaunchedCollectWithLifecycle(viewModel.events) {
        when (it) {
            MeasurementEvent.Deleted -> latestOnDelete()
            MeasurementEvent.Saved -> latestOnUpdateMeasurement()
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
            onDelete = viewModel::deleteProduct,
            onMeasure = viewModel::updateMeasurement,
            onUnpack = viewModel::unpack,
            food = food,
            foodEvents = productEvents,
            today = today,
            selectedDate = measurementDate,
            meals = meals,
            selectedMeal = meals.first { it.id == mealId },
            suggestions = suggestions,
            possibleTypes = possibleTypes,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
            selectedMeasurement = selectedMeasurement
        )
    }
}
