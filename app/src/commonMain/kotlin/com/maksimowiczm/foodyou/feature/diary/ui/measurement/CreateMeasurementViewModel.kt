package com.maksimowiczm.foodyou.feature.diary.ui.measurement

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class CreateMeasurementViewModel(
    productRepository: ProductRepository,
    private val addFoodRepository: AddFoodRepository,
    private val mealId: Long,
    private val date: LocalDate,
    productId: Long
) : MeasurementViewModel(productRepository) {
    private val _uiEvent = MutableStateFlow<MeasurementEvent>(MeasurementEvent.Empty)
    override val uiEvent = _uiEvent

    private val observeProductJob: Job = combine(
        productRepository.observeProductById(productId),
        addFoodRepository.observeQuantitySuggestionByProductId(productId)
    ) { product, suggestion ->
        if (product == null) {
            _uiEvent.value =
                MeasurementEvent.Error
            return@combine
        }

        _uiEvent.value =
            MeasurementEvent.Ready(
                product = product,
                suggestion = suggestion
            )
    }.catch {
        _uiEvent.value =
            MeasurementEvent.Error
    }.launchIn(viewModelScope)

    override fun onAddMeasurement(weightMeasurementEnum: WeightMeasurementEnum, quantity: Float) {
        val uiState = _uiEvent.value

        if (uiState !is MeasurementEvent.Ready) {
            return
        }

        observeProductJob.cancel()

        _uiEvent.value =
            MeasurementEvent.Processing

        val weightMeasurement = when (weightMeasurementEnum) {
            WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(quantity)
            WeightMeasurementEnum.Package -> {
                val packageWeight = uiState.product.packageWeight

                if (packageWeight == null) {
                    _uiEvent.value =
                        MeasurementEvent.Error
                    return
                }

                WeightMeasurement.Package(
                    quantity = quantity,
                    packageWeight = packageWeight
                )
            }

            WeightMeasurementEnum.Serving -> {
                val servingWeight = uiState.product.servingWeight

                if (servingWeight == null) {
                    _uiEvent.value =
                        MeasurementEvent.Error
                    return
                }

                WeightMeasurement.Serving(
                    quantity = quantity,
                    servingWeight = servingWeight
                )
            }
        }

        viewModelScope.launch {
            addFoodRepository.addMeasurement(
                date = date,
                mealId = mealId,
                productId = uiState.product.id,
                weightMeasurement = weightMeasurement
            )

            _uiEvent.value =
                MeasurementEvent.Success
        }
    }
}
