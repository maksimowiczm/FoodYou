package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.data.AddFoodRepository
import com.maksimowiczm.foodyou.data.ProductRepository
import com.maksimowiczm.foodyou.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.data.model.WeightMeasurementEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UpdateMeasurementViewModel(
    productRepository: ProductRepository,
    private val addFoodRepository: AddFoodRepository,
    private val measurementId: Long
) : MeasurementViewModel(productRepository) {
    private val _uiEvent = MutableStateFlow<MeasurementEvent>(MeasurementEvent.Empty)
    override val uiEvent: StateFlow<MeasurementEvent> = _uiEvent

    private val observeProductJob =
        addFoodRepository.observeProductByMeasurementId(measurementId).onEach { product ->
            if (product == null) {
                _uiEvent.value =
                    MeasurementEvent.Error
                return@onEach
            }

            addFoodRepository
                .observeQuantitySuggestionByProductId(product.product.id)
                .map { it }
                .collectLatest {
                    val enum = product.measurement.asEnum()
                    val suggestion = it.replace(product.measurement)

                    _uiEvent.value =
                        MeasurementEvent.Ready(
                            product = product.product,
                            suggestion = suggestion,
                            highlight = enum
                        )
                }
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
            addFoodRepository.updateMeasurement(
                measurementId = measurementId,
                weightMeasurement = weightMeasurement
            )

            _uiEvent.value =
                MeasurementEvent.Success
        }
    }
}
