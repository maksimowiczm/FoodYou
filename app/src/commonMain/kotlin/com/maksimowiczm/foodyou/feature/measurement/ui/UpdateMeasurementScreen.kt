package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.rememberAdvancedMeasurementFormState
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateMeasurementScreen(
    measurementId: MeasurementId,
    onBack: () -> Unit,
    modifier: Modifier = Modifier.Companion,
    viewModel: UpdateMeasurementViewModel = koinViewModel(
        parameters = { parametersOf(measurementId) }
    )
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val measurement = viewModel.measurement.collectAsStateWithLifecycle().value
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value

    if (measurement == null || meals == null || suggestions == null) {
        // TODO
        return
    }

    val latestOnBack by rememberUpdatedState(onBack)
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.measurementUpdatedEventBus.collectLatest {
                latestOnBack()
            }
        }
    }

    val formState = rememberAdvancedMeasurementFormState(
        food = measurement.food,
        initialDate = measurement.measurementDate.date,
        meals = meals,
        measurements = (listOf(measurement.measurement) + suggestions).distinct(),
        initialMeal = measurement.mealId.let { meals.indexOfFirst { meal -> meal.id == it } },
        initialMeasurement = 0
    )

    MeasurementScreen(
        state = formState,
        food = measurement.food,
        onBack = onBack,
        onSave = {
            val date = formState.date
            val measurement = formState.measurement
            val mealId = formState.meal?.id

            if (measurement != null && mealId != null) {
                viewModel.onUpdateMeasurement(
                    date = date,
                    mealId = mealId,
                    measurement = measurement
                )
            }
        },
        modifier = modifier
    )
}
