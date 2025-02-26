package com.maksimowiczm.foodyou.feature.legacy.addfood.ui.portion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.data.AddFoodRepository
import com.maksimowiczm.foodyou.data.ProductRepository
import com.maksimowiczm.foodyou.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.legacy.addfood.PortionFeature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class UpdatePortionViewModel(
    productRepository: ProductRepository,
    private val addFoodRepository: AddFoodRepository,
    savedStateHandle: SavedStateHandle
) : PortionViewModel(productRepository) {
    val mealId: Long
    val date: LocalDate
    val measurementId: Long

    private val _uiEvent =
        MutableStateFlow<PortionEvent>(
            PortionEvent.Empty
        )
    override val uiEvent:
        StateFlow<PortionEvent> = _uiEvent

    init {
        val (epochDay, meal, measurementId) = savedStateHandle.toRoute<PortionFeature.Edit>()

        this.mealId = meal
        this.date = LocalDate.fromEpochDays(epochDay)
        this.measurementId = measurementId
    }

    private val observeProductJob =
        addFoodRepository.observeProductByMeasurementId(measurementId).onEach { product ->
            if (product == null) {
                _uiEvent.value =
                    PortionEvent.Error
                return@onEach
            }

            addFoodRepository
                .observeQuantitySuggestionByProductId(product.product.id)
                .map { it }
                .collectLatest {
                    val enum = product.measurement.asEnum()
                    val suggestion = it.replace(product.measurement)

                    _uiEvent.value =
                        PortionEvent.Ready(
                            product = product.product,
                            suggestion = suggestion,
                            highlight = enum
                        )
                }
        }.catch {
            _uiEvent.value =
                PortionEvent.Error
        }.launchIn(viewModelScope)

    override fun onAddPortion(weightMeasurementEnum: WeightMeasurementEnum, quantity: Float) {
        val uiState = _uiEvent.value

        if (uiState !is PortionEvent.Ready) {
            return
        }

        observeProductJob.cancel()

        _uiEvent.value =
            PortionEvent.Processing

        val weightMeasurement = when (weightMeasurementEnum) {
            WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(quantity)
            WeightMeasurementEnum.Package -> {
                val packageWeight = uiState.product.packageWeight

                if (packageWeight == null) {
                    _uiEvent.value =
                        PortionEvent.Error
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
                        PortionEvent.Error
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
                PortionEvent.Success
        }
    }
}
