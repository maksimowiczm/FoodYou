package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.compose.AddProductScreen
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
    onEditProduct: (productId: Long) -> Unit
) {
    crossfadeComposable<CreateFoodProductMeasurement> {
        val (productId) = it.toRoute<CreateFoodProductMeasurement>()

        AddProductScreen(
            onBack = onCreateBack,
            onEditProduct = onEditProduct,
            onConfirm = {
                onCreate(FoodId.Product(productId), it)
            },
            viewModel = koinViewModel<CreateMeasurementViewModel>(
                parameters = { parametersOf(FoodId.Product(productId)) }
            )
        )
    }
    crossfadeComposable<EditFoodProductMeasurement> {
        val (measurementId) = it.toRoute<EditFoodProductMeasurement>()

        AddProductScreen(
            onBack = onEditBack,
            onEditProduct = onEditProduct,
            onConfirm = {
                onEdit(MeasurementId.Product(measurementId), it)
            },
            viewModel = koinViewModel<UpdateMeasurementViewModel>(
                parameters = { parametersOf(MeasurementId.Product(measurementId)) }
            )
        )
    }
}
