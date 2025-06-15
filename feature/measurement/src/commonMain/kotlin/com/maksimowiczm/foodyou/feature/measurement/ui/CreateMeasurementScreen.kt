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
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.rememberAdvancedMeasurementFormState
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
    onEditFood: (FoodId) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<CreateMeasurementViewModel>(
        parameters = { parametersOf(foodId) }
    )

    val food = viewModel.food.collectAsStateWithLifecycle().value
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value
    val date = date ?: LocalDate.now(TimeZone.currentSystemDefault())

    val latestOnBack by rememberUpdatedState(onBack)
    LaunchedCollectWithLifecycle(viewModel.measurementCreatedEventBus) {
        when (it) {
            MeasurementScreenEvent.FoodDeleted -> latestOnBack()
            MeasurementScreenEvent.Done -> latestOnBack()
        }
    }

    if (food == null || meals == null || suggestions == null) {
        // TODO loading state
        Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        return
    }

    val formState = rememberAdvancedMeasurementFormState(
        food = food,
        initialDate = date,
        meals = meals,
        measurements = suggestions,
        initialMeal = mealId?.let { meals.indexOfFirst { meal -> meal.id == it } },
        initialMeasurement = 0
    )

    MeasurementScreen(
        state = formState,
        food = food,
        onBack = onBack,
        onSave = {
            val date = formState.date
            val measurement = formState.measurement
            val mealId = formState.meal?.id

            if (measurement != null && mealId != null) {
                viewModel.onCreateMeasurement(
                    date = date,
                    mealId = mealId,
                    measurement = measurement
                )
            }
        },
        onEditFood = { onEditFood(foodId) },
        onDeleteFood = viewModel::onDeleteMeasurement,
        onIngredientClick = { onEditFood(it) },
        onUnpack = {
            val date = formState.date
            val measurement = formState.measurement
            val mealId = formState.meal?.id

            if (measurement != null && mealId != null) {
                viewModel.unpackRecipe(
                    date = date,
                    mealId = mealId,
                    measurement = measurement
                )
            }
        },
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
    )
}
