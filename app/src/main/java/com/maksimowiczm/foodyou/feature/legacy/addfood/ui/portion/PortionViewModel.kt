package com.maksimowiczm.foodyou.feature.legacy.addfood.ui.portion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.data.ProductRepository
import com.maksimowiczm.foodyou.data.model.WeightMeasurementEnum
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class PortionViewModel(private val productRepository: ProductRepository) : ViewModel() {
    abstract val uiEvent:
        StateFlow<PortionEvent>
    abstract fun onAddPortion(weightMeasurementEnum: WeightMeasurementEnum, quantity: Float)

    fun onProductDelete(productId: Long) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
        }
    }
}
