package com.maksimowiczm.foodyou.feature.addfood.ui.portion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.addfood.PortionFeature
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.product.data.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class CreatePortionViewModel(
    productRepository: ProductRepository,
    private val addFoodRepository: AddFoodRepository,
    savedStateHandle: SavedStateHandle
) : PortionViewModel(productRepository) {
    val mealId: Long
    val date: LocalDate
    val productId: Long

    private val _uiEvent = MutableStateFlow<PortionEvent>(PortionEvent.Empty)
    override val uiEvent = _uiEvent

    init {
        val (epochDay, meal, productId) = savedStateHandle.toRoute<PortionFeature.Create>()

        this.mealId = meal
        this.date = LocalDate.fromEpochDays(epochDay)
        this.productId = productId
    }

    private val observeProductJob: Job = combine(
        productRepository.observeProductById(productId),
        addFoodRepository.observeQuantitySuggestionByProductId(productId)
    ) { product, suggestion ->
        if (product == null) {
            _uiEvent.value = PortionEvent.Error
            return@combine
        }

        _uiEvent.value = PortionEvent.Ready(
            product = product,
            suggestion = suggestion
        )
    }.catch {
        _uiEvent.value = PortionEvent.Error
    }.launchIn(viewModelScope)

    override fun onAddPortion(weightMeasurementEnum: WeightMeasurementEnum, quantity: Float) {
        val uiState = _uiEvent.value

        if (uiState !is PortionEvent.Ready) {
            return
        }

        observeProductJob.cancel()

        _uiEvent.value = PortionEvent.Processing

        val weightMeasurement = when (weightMeasurementEnum) {
            WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(quantity)
            WeightMeasurementEnum.Package -> {
                val packageWeight = uiState.product.packageWeight

                if (packageWeight == null) {
                    _uiEvent.value = PortionEvent.Error
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
                    _uiEvent.value = PortionEvent.Error
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

            _uiEvent.value = PortionEvent.Success
        }
    }
}
