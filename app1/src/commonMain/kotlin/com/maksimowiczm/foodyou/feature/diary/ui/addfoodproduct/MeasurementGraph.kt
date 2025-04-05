package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.compose.AddProductScreen
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class CreateFoodProductMeasurement(val productId: Long)

@Serializable
data class EditFoodProductMeasurement(val productMeasurementId: Long)

fun NavGraphBuilder.measurementGraph(
    mealId: Long,
    date: LocalDate,
    onCreateBack: () -> Unit,
    onEditBack: () -> Unit,
    onEditProduct: (productId: Long) -> Unit
) {
    crossfadeComposable<CreateFoodProductMeasurement> {
        val (productId) = it.toRoute<CreateFoodProductMeasurement>()

        AddProductScreen(
            onBack = onCreateBack,
            onEditProduct = onEditProduct,
            viewModel = koinViewModel<CreateMeasurementViewModel>(
                parameters = { parametersOf(productId, mealId, date) }
            )
        )
    }
    crossfadeComposable<EditFoodProductMeasurement> {
        val (measurementId) = it.toRoute<EditFoodProductMeasurement>()

        AddProductScreen(
            onBack = onEditBack,
            onEditProduct = onEditProduct,
            viewModel = koinViewModel<UpdateMeasurementViewModel>(
                parameters = { parametersOf(MeasurementId.Product(measurementId)) }
            )
        )
    }
}
