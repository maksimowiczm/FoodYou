package com.maksimowiczm.foodyou.feature.addfood.ui.measurement

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateMeasurementScreen(
    measurementId: MeasurementId,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (FoodId) -> Unit,
    onCloneRecipe: (newId: FoodId.Product) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateMeasurementScreenViewModel = koinViewModel(
        parameters = { parametersOf(measurementId) }
    )
) {
    val onBack by rememberUpdatedState(onBack)
    val onDelete by rememberUpdatedState(onDelete)
    val onCloneRecipe by rememberUpdatedState(onCloneRecipe)

    LaunchedEffect(viewModel) {
        viewModel.eventBus.collectLatest {
            when (it) {
                is MeasurementScreenEvent.Closed -> onBack()
                is MeasurementScreenEvent.FoodDeleted -> onDelete()
                is MeasurementScreenEvent.RecipeClonedIntoProduct -> onCloneRecipe(it.newId)
            }
        }
    }

    val food by viewModel.food.collectAsStateWithLifecycle()
    val selectedMeasurement by viewModel.selectedMeasurement.collectAsStateWithLifecycle()

    val suffix = stringResource(Res.string.headline_copy)

    when (val food = food) {
        null -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        else -> MeasurementScreen(
            food = food.food,
            suggestions = food.suggestions,
            selected = selectedMeasurement ?: food.selected,
            onBack = onBack,
            onMeasurement = remember(viewModel) { viewModel::onConfirm },
            onEditFood = { onEdit(food.food.id) },
            onDeleteFood = { viewModel.onDeleteFood(food.food.id) },
            onCloneRecipe = { viewModel.onCloneRecipe(it, suffix) },
            modifier = modifier
        )
    }
}
