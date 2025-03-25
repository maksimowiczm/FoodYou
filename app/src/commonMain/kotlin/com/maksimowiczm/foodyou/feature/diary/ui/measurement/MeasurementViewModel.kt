package com.maksimowiczm.foodyou.feature.diary.ui.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.ProductRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class MeasurementViewModel(private val productRepository: ProductRepository) :
    ViewModel() {
    abstract val uiEvent: StateFlow<MeasurementEvent>
    abstract fun onAddMeasurement(weightMeasurementEnum: WeightMeasurementEnum, quantity: Float)

    fun onProductDelete(productId: Long) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
        }
    }
}
