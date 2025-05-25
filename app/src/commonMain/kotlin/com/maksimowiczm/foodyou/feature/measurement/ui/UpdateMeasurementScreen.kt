package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.model.Recipe
import com.maksimowiczm.foodyou.core.ui.ext.collectWithLifecycle
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.rememberAdvancedMeasurementFormState
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_copy
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateMeasurementScreen(
    measurementId: MeasurementId,
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onRecipeClone: (FoodId.Product) -> Unit,
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
        // TODO
        return
    }

    val onEvent: (MeasurementScreenEvent) -> Unit = remember(onBack, onRecipeClone) {
        {
            when (it) {
                MeasurementScreenEvent.Deleted -> onBack()
                MeasurementScreenEvent.Done -> onBack()
                is MeasurementScreenEvent.RecipeCloned -> onRecipeClone(it.productId)
            }
        }
    }
    viewModel.measurementUpdatedEventBus.collectWithLifecycle { onEvent(it) }

    val formState = rememberAdvancedMeasurementFormState(
        food = measurement.food,
        initialDate = measurement.measurementDate.date,
        meals = meals,
        measurements = (listOf(measurement.measurement) + suggestions).distinct(),
        initialMeal = measurement.mealId.let { meals.indexOfFirst { meal -> meal.id == it } },
        initialMeasurement = 0
    )

    val copySuffix = stringResource(Res.string.headline_copy)

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
        onEditFood = { onEditFood(measurement.food.id) },
        onDelete = viewModel::onDeleteMeasurement,
        onClone = if (measurement.food is Recipe) {
            {
                viewModel.onRecipeClone(
                    recipeId = (measurement.food as Recipe).id,
                    suffix = copySuffix
                )
            }
        } else {
            null
        },
        onIngredientClick = { onEditFood(it) },
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
    )
}
