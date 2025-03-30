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
data class CreateProductMeasurement(val productId: Long)

@Serializable
data class CreateRecipeMeasurement(val productId: Long)

@Serializable
data class EditProductMeasurement(val productMeasurementId: Long)

@Serializable
data class EditRecipeMeasurement(val recipeMeasurementId: Long)

fun NavGraphBuilder.measurementGraph(
    onCreateBack: () -> Unit,
    onCreate: (FoodId, WeightMeasurement) -> Unit,
    onEditBack: () -> Unit,
    onEdit: (MeasurementId, WeightMeasurement) -> Unit,
    onEditFood: (FoodId) -> Unit,
    onDeleteFood: (FoodId) -> Unit
) {
    crossfadeComposable<CreateProductMeasurement> {
        val (productId) = it.toRoute<CreateProductMeasurement>()

        MeasurementFormScreen(
            foodId = FoodId.Product(productId),
            onEditFood = onEditFood,
            onDeleteFood = onDeleteFood,
            onBack = onCreateBack,
            onConfirm = { onCreate(FoodId.Product(productId), it) }
        )
    }
    crossfadeComposable<CreateRecipeMeasurement> {
        val (recipeId) = it.toRoute<CreateRecipeMeasurement>()

        MeasurementFormScreen(
            foodId = FoodId.Recipe(recipeId),
            onEditFood = onEditFood,
            onDeleteFood = onDeleteFood,
            onBack = onCreateBack,
            onConfirm = { onCreate(FoodId.Recipe(recipeId), it) }
        )
    }
    crossfadeComposable<EditProductMeasurement> {
        val (measurementId) = it.toRoute<EditProductMeasurement>()

        MeasurementFormScreen(
            measurementId = MeasurementId.Product(measurementId),
            onEditFood = onEditFood,
            onDeleteFood = onDeleteFood,
            onBack = onEditBack,
            onConfirm = { onEdit(MeasurementId.Product(measurementId), it) }
        )
    }
    crossfadeComposable<EditRecipeMeasurement> {
        val (measurementId) = it.toRoute<EditRecipeMeasurement>()

        MeasurementFormScreen(
            measurementId = MeasurementId.Recipe(measurementId),
            onEditFood = onEditFood,
            onDeleteFood = onDeleteFood,
            onBack = onEditBack,
            onConfirm = { onEdit(MeasurementId.Recipe(measurementId), it) }
        )
    }
}
