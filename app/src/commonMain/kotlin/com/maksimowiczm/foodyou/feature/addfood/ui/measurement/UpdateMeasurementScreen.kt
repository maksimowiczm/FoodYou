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
import com.maksimowiczm.foodyou.core.model.MeasurementId
import com.maksimowiczm.foodyou.feature.measurement.MeasurementScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateMeasurementScreen(
    measurementId: MeasurementId,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateMeasurementScreenViewModel = koinViewModel(
        parameters = { parametersOf(measurementId) }
    )
) {
    val onBack by rememberUpdatedState(onBack)
    val onDelete by rememberUpdatedState(onDelete)

    LaunchedEffect(viewModel) {
        viewModel.eventBus.collectLatest {
            when (it) {
                is MeasurementScreenEvent.Closed -> onBack()
                is MeasurementScreenEvent.FoodDeleted -> onDelete()
            }
        }
    }

    val food by viewModel.food.collectAsStateWithLifecycle()
    val selectedMeasurement by viewModel.selectedMeasurement.collectAsStateWithLifecycle()

    when (val food = food) {
        null -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        else -> MeasurementScreen(
            food = food,
            selectedMeasurement = selectedMeasurement ?: food.selected,
            onBack = onBack,
            onMeasurement = remember(viewModel) { viewModel::onConfirm },
            onEditFood = onEdit,
            onDeleteFood = { viewModel.onDeleteFood(food.food.id) },
            modifier = modifier
        )
    }
}
