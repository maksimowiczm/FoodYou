package com.maksimowiczm.foodyou.core.feature.addfood.ui.portion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.AddFoodFeature
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class PortionViewModel(
    private val productRepository: ProductRepository,
    private val addFoodRepository: AddFoodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val mealId: Long
    val date: LocalDate

    init {
        val (epochDay, meal, productId) = savedStateHandle.toRoute<AddFoodFeature>()

        this.mealId = meal
        this.date = LocalDate.fromEpochDays(epochDay)

        if (productId != null) {
            loadProduct(productId)
        }
    }

    private val _uiState = MutableStateFlow<PortionUiState>(PortionUiState.Empty)
    val uiState = _uiState.asStateFlow()

    fun loadProduct(id: Long) {
        _uiState.value = PortionUiState.Loading

        viewModelScope.launch {
            val product = productRepository.getProductById(id)
            val suggestion = addFoodRepository.getQuantitySuggestionByProductId(id)

            if (product == null) {
                _uiState.value = PortionUiState.Error
                return@launch
            }

            _uiState.value = PortionUiState.Ready(
                product = product,
                suggestion = suggestion
            )
        }
    }

    fun onAddPortion(
        weightMeasurementEnum: WeightMeasurementEnum,
        quantity: Float
    ) {
        val uiState = _uiState.value

        if (uiState !is PortionUiState.Ready) {
            return
        }

        _uiState.value = PortionUiState.CreatingPortion(
            product = uiState.product,
            suggestion = uiState.suggestion,
            measurement = weightMeasurementEnum
        )

        viewModelScope.launch {
            addFoodRepository.addFood(
                date = date,
                mealId = mealId,
                productId = uiState.product.id,
                weightMeasurement = weightMeasurementEnum,
                quantity = quantity
            )

            _uiState.value = PortionUiState.Success(
                product = uiState.product,
                suggestion = uiState.suggestion,
                measurement = weightMeasurementEnum
            )
        }
    }
}
