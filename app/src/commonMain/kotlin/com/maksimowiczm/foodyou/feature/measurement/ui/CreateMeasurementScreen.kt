package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.core.ui.ext.collectWithLifecycle
import com.maksimowiczm.foodyou.feature.measurement.ui.advanced.rememberAdvancedMeasurementFormState
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_copy
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun CreateMeasurementScreen(
    foodId: FoodId,
    mealId: Long?,
    date: LocalDate?,
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onRecipeClone: (FoodId.Product) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: CreateMeasurementViewModel = koinViewModel(
        parameters = { parametersOf(foodId) }
    )
) {
    val food = viewModel.food.collectAsStateWithLifecycle().value
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value

    val onEvent: (MeasurementScreenEvent) -> Unit = remember(onBack, onRecipeClone) {
        {
            when (it) {
                MeasurementScreenEvent.Deleted -> onBack()
                MeasurementScreenEvent.Done -> onBack()
                is MeasurementScreenEvent.RecipeCloned -> onRecipeClone(it.productId)
            }
        }
    }
    viewModel.measurementCreatedEventBus.collectWithLifecycle { onEvent(it) }

    if (food == null || meals == null || suggestions == null) {
        // TODO
        return
    }

    val formState = rememberAdvancedMeasurementFormState(
        food = food,
        initialDate = date ?: LocalDate.now(TimeZone.currentSystemDefault()),
        meals = meals,
        measurements = suggestions,
        initialMeal = mealId?.let { meals.indexOfFirst { meal -> meal.id == it } },
        initialMeasurement = 0
    )

    val copySuffix = stringResource(Res.string.headline_copy)

    MeasurementScreen(
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
        onEditFood = { onEditFood(foodId) },
        onDelete = viewModel::onDeleteMeasurement,
        onClone = if (foodId is FoodId.Recipe) {
            { viewModel.onRecipeClone(recipeId = foodId, suffix = copySuffix) }
        } else {
            null
        },
        onIngredientClick = { onEditFood(it) },
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier
    )
}
