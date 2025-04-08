package com.maksimowiczm.foodyou.feature.diary.addfood.measurement

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import com.maksimowiczm.foodyou.feature.diary.addfood.measurement.ui.CreateMeasurementScreenViewModel
import com.maksimowiczm.foodyou.feature.diary.addfood.measurement.ui.MeasurementScreen
import com.maksimowiczm.foodyou.feature.diary.addfood.measurement.ui.UpdateMeasurementScreenViewModel
import com.maksimowiczm.foodyou.feature.diary.core.data.food.FoodId
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementId
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class MeasureProduct(val productId: Long)

@Serializable
data class UpdateProductMeasurement(val measurementId: Long)

fun NavGraphBuilder.measurementGraph(
    date: LocalDate,
    mealId: Long,
    onCreateProductMeasurementBack: () -> Unit,
    onCreateProductMeasurement: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onUpdateProductMeasurementBack: () -> Unit,
    onUpdateProductMeasurement: () -> Unit
) {
    crossfadeComposable<MeasureProduct> {
        val (productId) = it.toRoute<MeasureProduct>()

        MeasurementScreen(
            viewModel = koinViewModel<CreateMeasurementScreenViewModel>(
                parameters = { parametersOf(date, mealId, FoodId.Product(productId)) }
            ),
            onBack = onCreateProductMeasurementBack,
            onCreateMeasurement = onCreateProductMeasurement,
            onEditFood = onEditFood,
            onDeleteFood = onCreateProductMeasurementBack
        )
    }
    crossfadeComposable<UpdateProductMeasurement> {
        val (measurementId) = it.toRoute<UpdateProductMeasurement>()

        MeasurementScreen(
            viewModel = koinViewModel<UpdateMeasurementScreenViewModel>(
                parameters = { parametersOf(MeasurementId.Product(measurementId)) }
            ),
            onBack = onUpdateProductMeasurementBack,
            onCreateMeasurement = onUpdateProductMeasurement,
            onEditFood = onEditFood,
            onDeleteFood = onUpdateProductMeasurement
        )
    }
}
