package com.maksimowiczm.foodyou.feature.diary.ui.measurement

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.compose.MeasurementFormScreen
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import kotlinx.serialization.Serializable

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

        MeasurementFormScreen(
            foodId = FoodId.Product(productId),
            onEditFood = onEditFood,
            onDeleteFood = onDeleteFood,
            onBack = onCreateBack,
            onConfirm = { onCreate(FoodId.Product(productId), it) }
        )
    }
    crossfadeComposable<EditFoodProductMeasurement> {
        val (measurementId) = it.toRoute<EditFoodProductMeasurement>()

        MeasurementFormScreen(
            measurementId = MeasurementId.Product(measurementId),
            onEditFood = onEditFood,
            onDeleteFood = onDeleteFood,
            onBack = onEditBack,
            onConfirm = { onEdit(MeasurementId.Product(measurementId), it) }
        )
    }
}
