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
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.rememberAdvancedMeasurementFormState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateMeasurementScreen(
    measurementId: MeasurementId,
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onRecipeClone: (FoodId.Product, mealId: Long?, epochDay: Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: UpdateMeasurementViewModel = koinViewModel(
        parameters = { parametersOf(measurementId) }
    )
) {
    val measurement = viewModel.measurement.collectAsStateWithLifecycle().value
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value

    if (measurement == null || meals == null || suggestions == null) {
        // TODO loading state
        Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        return
    }

    val latestOnBack by rememberUpdatedState(onBack)
    val latestOnRecipeClone by rememberUpdatedState(onRecipeClone)
    LaunchedCollectWithLifecycle(viewModel.measurementUpdatedEventBus) {
        when (it) {
            MeasurementScreenEvent.Deleted -> latestOnBack()
            MeasurementScreenEvent.Done -> latestOnBack()
            is MeasurementScreenEvent.RecipeCloned -> latestOnRecipeClone(
                it.productId,
                measurement.mealId,
                measurement.measurementDate.date.toEpochDays()
            )
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
        onDelete = viewModel::onDeleteMeasurement,
        onIngredientClick = { onEditFood(it) },
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
    )
}
