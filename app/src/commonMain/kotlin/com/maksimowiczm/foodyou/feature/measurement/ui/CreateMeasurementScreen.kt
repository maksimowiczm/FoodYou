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
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_copy
import kotlinx.coroutines.flow.collectLatest
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
    onEditFood: () -> Unit,
    onRecipeClone: (FoodId.Product) -> Unit,
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
    val latestOnRecipeCloned by rememberUpdatedState(onRecipeClone)
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.measurementCreatedEventBus.collectLatest { event ->
                when (event) {
                    MeasurementScreenEvent.Deleted -> latestOnBack()
                    MeasurementScreenEvent.Done -> latestOnBack()
                    is MeasurementScreenEvent.RecipeCloned -> latestOnRecipeCloned(event.productId)
                }
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
        onEditFood = onEditFood,
        onDelete = viewModel::onDeleteMeasurement,
        onClone = if (foodId is FoodId.Recipe) {
            { viewModel.onRecipeClone(recipeId = foodId, suffix = copySuffix) }
        } else {
            null
        },
        modifier = modifier
    )
}
