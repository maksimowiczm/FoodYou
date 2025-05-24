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
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.rememberAdvancedMeasurementFormState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun CreateMeasurementScreen(
    foodId: FoodId,
    mealId: Long?,
    date: LocalDate?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier.Companion,
    viewModel: CreateMeasurementViewModel = koinViewModel(
        parameters = { parametersOf(foodId) }
    )
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val food = viewModel.food.collectAsStateWithLifecycle().value
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value

    val latestOnBack by rememberUpdatedState(onBack)
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.measurementCreatedEventBus.collectLatest {
                latestOnBack()
            }
        }
    }

    if (food == null || meals == null || suggestions == null) {
        // TODO
        return
    }

    val formState = rememberAdvancedMeasurementFormState(
        food = food,
        initialDate = date ?: LocalDate.Companion.now(TimeZone.Companion.currentSystemDefault()),
        meals = meals,
        measurements = suggestions,
        initialMeal = mealId?.let { meals.indexOfFirst { meal -> meal.id == it } },
        initialMeasurement = null
    )

    CreateMeasurementScreen(
        state = formState,
        food = food,
        onBack = onBack,
        onSave = {
            val measurement = formState.measurement
            val mealId = formState.meal?.id

            if (measurement != null && mealId != null) {
                viewModel.onCreateMeasurement(
                    date = formState.date,
                    mealId = mealId,
                    measurement = measurement
                )
            }
        },
        modifier = modifier
    )
}
