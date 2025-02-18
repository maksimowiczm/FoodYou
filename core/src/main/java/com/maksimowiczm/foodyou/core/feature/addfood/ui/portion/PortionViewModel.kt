package com.maksimowiczm.foodyou.core.feature.addfood.ui.portion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.AddFoodFeature
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
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

    private val _uiEvent = MutableStateFlow<PortionEvent>(PortionEvent.Empty)
    val uiEvent = _uiEvent.asStateFlow()

    private var observeProductJob: Job? = null

    /**
     * Load the product with the given ID and its quantity suggestion. The UI will be notified
     * with the [PortionEvent]s.
     */
    fun loadProduct(id: Long) {
        _uiEvent.value = PortionEvent.Loading

        observeProductJob?.cancel()
        observeProductJob = combine(
            productRepository.observeProductById(id),
            addFoodRepository.observeQuantitySuggestionByProductId(id)
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
    }

    fun onAddPortion(
        weightMeasurementEnum: WeightMeasurementEnum,
        quantity: Float
    ) {
        val uiState = _uiEvent.value

        if (uiState !is PortionEvent.Ready) {
            return
        }

        observeProductJob?.cancel()

        _uiEvent.value = PortionEvent.CreatingPortion

        viewModelScope.launch {
            addFoodRepository.addFood(
                date = date,
                mealId = mealId,
                productId = uiState.product.id,
                weightMeasurement = weightMeasurementEnum,
                quantity = quantity
            )

            _uiEvent.value = PortionEvent.Success
        }
    }
}
