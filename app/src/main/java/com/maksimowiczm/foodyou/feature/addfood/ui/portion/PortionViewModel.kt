package com.maksimowiczm.foodyou.feature.addfood.ui.portion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.product.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class PortionViewModel(
    private val addFoodRepository: AddFoodRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    sealed interface State {
        data object Nothing : State
        data class Create(val productId: Long, val date: LocalDate, val meal: Meal) : State
    }

    private var state: State = State.Nothing

    private val _uiState =
        MutableStateFlow<PortionUiState>(PortionUiState.WaitingForProduct)
    val uiState = _uiState.asStateFlow()

    fun load(
        productId: Long,
        date: LocalDate,
        meal: Meal
    ) {
        state = State.Create(productId, date, meal)

        _uiState.value = PortionUiState.LoadingProduct

        viewModelScope.launch {
            val product = productRepository.getProductById(productId)
            val suggestion = addFoodRepository.getQuantitySuggestionByProductId(productId)

            if (product == null) {
                _uiState.value = PortionUiState.ProductNotFound
                return@launch
            }

            _uiState.value = PortionUiState.ProductReady(
                product = product,
                suggestion = suggestion,
                highlight = null
            )
        }
    }

    fun onSave(weightMeasurementEnum: WeightMeasurementEnum, quantity: Float) {
        val uiState = _uiState.value

        if (uiState !is PortionUiState.ProductReady) {
            return
        }

        if (state !is State.Create) {
            return
        }

        val (productId, date, meal) = state as State.Create

        _uiState.value = PortionUiState.CreatingPortion(
            product = uiState.product,
            suggestion = uiState.suggestion,
            highlight = uiState.highlight
        )

        viewModelScope.launch {
            addFoodRepository.addFood(
                date = date,
                meal = meal,
                productId = productId,
                weightMeasurement = weightMeasurementEnum,
                quantity = quantity
            )

            _uiState.value = PortionUiState.Success(
                product = uiState.product,
                suggestion = uiState.suggestion,
                highlight = weightMeasurementEnum
            )
        }
    }
}
