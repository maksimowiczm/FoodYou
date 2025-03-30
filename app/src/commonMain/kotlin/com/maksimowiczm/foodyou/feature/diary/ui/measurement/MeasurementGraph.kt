package com.maksimowiczm.foodyou.feature.diary.ui.measurement

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.compose.MeasurementForm
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.compose.rememberMeasurementFormState
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class CreateFoodProductMeasurement(val productId: Long)

@Serializable
data class EditFoodProductMeasurement(val productMeasurementId: Long)

fun NavGraphBuilder.measurementGraph(
    onCreateBack: () -> Unit,
    onCreate: (FoodId, WeightMeasurement) -> Unit,
    onEditBack: () -> Unit,
    onEdit: (MeasurementId, WeightMeasurement) -> Unit,
    onEditFood: (FoodId) -> Unit,
    onDeleteFood: (FoodId) -> Unit
) {
    crossfadeComposable<CreateFoodProductMeasurement> {
        val (productId) = it.toRoute<CreateFoodProductMeasurement>()
        val viewModel = koinViewModel<CreateMeasurementViewModel>(
            parameters = { parametersOf(FoodId.Product(productId)) }
        )

        val productState = viewModel.food.collectAsStateWithLifecycle(null)
        val food = productState.value

        when (food) {
            null -> Surface(Modifier.fillMaxSize()) {}
            else -> {
                MeasurementForm(
                    name = food.name,
                    nutrients = food.nutrients,
                    packageWeight = food.packageWeight,
                    servingWeight = food.servingWeight,
                    state = rememberMeasurementFormState(
                        suggestion = food.suggestion
                    ),
                    onEdit = { onEditFood(FoodId.Product(productId)) },
                    onDelete = { onDeleteFood(FoodId.Product(productId)) },
                    onConfirm = { onCreate(FoodId.Product(productId), it) },
                    onBack = onCreateBack
                )
            }
        }
    }
    crossfadeComposable<EditFoodProductMeasurement> {
        val (measurementId) = it.toRoute<EditFoodProductMeasurement>()
        val viewModel = koinViewModel<UpdateMeasurementViewModel>(
            parameters = { parametersOf(MeasurementId.Product(measurementId)) }
        )

        val productState = viewModel.food.collectAsStateWithLifecycle(null)
        val food = productState.value

        when (food) {
            null -> Surface(Modifier.fillMaxSize()) {}
            else -> {
                MeasurementForm(
                    name = food.name,
                    nutrients = food.nutrients,
                    packageWeight = food.packageWeight,
                    servingWeight = food.servingWeight,
                    state = rememberMeasurementFormState(
                        suggestion = food.suggestion,
                        highlight = food.highlight
                    ),
                    onEdit = { onEditFood(food.id) },
                    onDelete = { onDeleteFood(food.id) },
                    onConfirm = { onEdit(MeasurementId.Product(measurementId), it) },
                    onBack = onEditBack
                )
            }
        }
    }
}
