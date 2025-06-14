package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.rememberAdvancedMeasurementFormState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateMeasurementScreen(
    measurementId: Long,
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<UpdateMeasurementViewModel>(
        parameters = { parametersOf(measurementId) }
    )

    val measurement = viewModel.measurement.collectAsStateWithLifecycle().value
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value

    val latestOnBack by rememberUpdatedState(onBack)
    LaunchedCollectWithLifecycle(viewModel.measurementUpdatedEventBus) {
        when (it) {
            MeasurementScreenEvent.FoodDeleted -> latestOnBack()
            MeasurementScreenEvent.Done -> latestOnBack()
        }
    }

    if (measurement == null || meals == null || suggestions == null) {
        // TODO loading state
        Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        return
    }

    val formState = rememberAdvancedMeasurementFormState(
        food = measurement.food,
        initialDate = measurement.measurementDate,
        meals = meals,
        measurements = (listOf(measurement.measurement) + suggestions).distinct(),
        initialMeal = measurement.mealId.let { meals.indexOfFirst { meal -> meal.id == it } },
        initialMeasurement = 0
    )

    val food = measurement.food

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
        onEditFood = { onEditFood(food.id) },
        onDeleteFood = viewModel::onDeleteFood,
        onIngredientClick = { onEditFood(it) },
        onUnpack = {
            val date = formState.date
            val mealId = formState.meal?.id
            val measurement = formState.measurement

            if (measurement != null && mealId != null) {
                viewModel.unpackRecipe(date, mealId, measurement)
            }
        },
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
    )
}
